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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.material.*
import com.olafros.wear.sanntid.components.StopPlaceChip
import com.olafros.wear.sanntid.components.StopPlaceChipData
import com.olafros.wear.sanntid.utils.Constants
import com.olafros.wear.sanntid.utils.Constants.ENTUR_API_URL
import com.olafros.wear.sanntid.utils.toJSONObjectList
import com.olafros.wear.sanntid.utils.toStringList
import com.olafros.wear.sanntid.utils.venueMapper
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class SearchViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
    var searchInputState by mutableStateOf(TextFieldValue())
    var data = mutableStateListOf<StopPlaceChipData>()
        private set

    fun search(input: String) {
        viewModelScope.launch {
            isLoading = true

            val request = Request.Builder()
                .url("$ENTUR_API_URL/geocoder/v1/autocomplete?lang=no&size=25&text=$input&layers=venue")
                .header("ET-Client-Name", Constants.ETClientName)
                .build()

            OkHttpClient().newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    isLoading = false
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")

                        val json = JSONObject(response.body!!.string())
                        val features = json.getJSONArray("features").toJSONObjectList()
                        data.clear()
                        features
                            .map { it.getJSONObject("properties") }
                            .forEach {

                                data.add(
                                    StopPlaceChipData(
                                        it.getString("id"),
                                        it.getString("label"),
                                        it.getJSONArray("category").toStringList()
                                            .map { cat -> venueMapper(cat) }.distinct().sorted()
                                            .joinToString(", ")
                                    )
                                )
                            }
                        isLoading = false
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
                                        MaterialTheme.colors.primary,
                                        RoundedCornerShape(percent = 50)
                                    )
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .focusRequester(focusRequester)
                            ) {
                                Icon(Icons.Rounded.Search, contentDescription = null)
                                Spacer(Modifier.width(16.dp))
                                Text(
                                    if (viewModel.searchInputState.text == "") "Søk" else viewModel.searchInputState.text,
                                    modifier = Modifier.padding(top = 2.dp),
                                    color = Color.White
                                )
                            }
                        }
                    )
                }
            }
            if (!viewModel.isLoading && viewModel.data.isEmpty()) {
                item {
                    Text(
                        if (viewModel.searchInputState.text == "") "Søk etter stoppesteder og se de neste avgangene derifra" else "Søket ditt ga ingen resultater, prøv igjen med andre søkeord",
                        textAlign = TextAlign.Center
                    )
                }
            }
            if (!viewModel.isLoading) {
                items(viewModel.data) {
                    StopPlaceChip(navController, it)
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
