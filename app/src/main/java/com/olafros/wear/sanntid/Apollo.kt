package com.olafros.wear.sanntid

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import com.olafros.wear.sanntid.utils.Constants
import com.olafros.wear.sanntid.utils.Constants.ENTUR_API_URL
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

private class AuthorizationInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("ET-Client-Name", Constants.ETClientName)
            .build()

        return chain.proceed(request)
    }
}

private var instance: ApolloClient? = null

fun apolloClient(): ApolloClient {
    if (instance != null) {
        return instance!!
    }

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthorizationInterceptor())
        .build()

    instance = ApolloClient.Builder()
        .serverUrl("$ENTUR_API_URL/journey-planner/v3/graphql")
        .okHttpClient(okHttpClient)
        .build()

    return instance!!
}
