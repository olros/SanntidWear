package com.olafros.wear.sanntid.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.material.*
import com.olafros.wear.sanntid.components.StopPlaceChip
import com.olafros.wear.sanntid.utils.Constants
import com.olafros.wear.sanntid.utils.venueMapper
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

data class SearchResult(val name: String, val id: String, val categories: List<String>)

class SearchViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
    var searchInputState by mutableStateOf(TextFieldValue())
    var data = mutableStateListOf<SearchResult>()
        private set

    fun search(input: String) {
        viewModelScope.launch {
            isLoading = true

            val request = Request.Builder()
                .url("https://api.entur.io/geocoder/v1/autocomplete?lang=no&size=25&text=$input&layers=venue")
                .header("ET-Client-Name", Constants.ETClientName)
                .build()

            OkHttpClient().newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    isLoading = false
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        isLoading = false
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")

                        data.clear()

                        val json = JSONObject(response.body!!.string())
                        val features = json.getJSONArray("features")
                        (0 until features.length()).forEach {
                            val properties = features.getJSONObject(it).getJSONObject("properties")
                            val id = properties.getString("id")
                            val label = properties.getString("label")
                            val category = properties.getJSONArray("category")

                            val categories = mutableListOf<String>()
                            (0 until category.length()).forEach { cat ->
                                categories.add(
                                    category.getString(
                                        cat
                                    )
                                )
                            }

                            data.add(
                                SearchResult(
                                    label,
                                    id,
                                    categories.map { cat -> venueMapper(cat) }.distinct())
                            )
                        }
                    }
                }
            })
        }
    }
}


@Composable
fun Search(
    navController: NavHostController,
    viewModel: SearchViewModel = viewModel()
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    fun onSearch() {
        viewModel.search(viewModel.searchInputState.text)
        focusManager.clearFocus()
    }

    Scaffold {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                ListHeader {
                    BasicTextField(
                        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        singleLine = true,
                        value = viewModel.searchInputState,
                        onValueChange = { viewModel.searchInputState = it },
                        decorationBox = {
                            Row(
                                Modifier
                                    .background(
                                        MaterialTheme.colors.secondary,
                                        RoundedCornerShape(percent = 50)
                                    )
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .focusRequester(focusRequester)
                            ) {
                                Icon(Icons.Rounded.Search, contentDescription = null)
                                Spacer(Modifier.width(16.dp))
                                Text(if (viewModel.searchInputState.text == "") "Søk" else viewModel.searchInputState.text)
                            }
                        }
                    )
                }
            }
            if (!viewModel.isLoading && viewModel.data.isEmpty()) {
                item {
                    Text(
                        "Søk opp stoppesteder og se de neste avgangene derifra",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            if (!viewModel.isLoading) {
                items(viewModel.data) {
                    StopPlaceChip(navController, it.id, it.name, it.categories.joinToString(", "))
                }
            }
            if (viewModel.isLoading) {
                item {
                    CircularProgressIndicator(strokeWidth = 4.dp)
                }
            }
        }
    }
}
