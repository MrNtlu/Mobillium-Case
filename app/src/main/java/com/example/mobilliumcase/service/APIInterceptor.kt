package com.example.mobilliumcase.service

import com.example.mobilliumcase.utils.Constants
import okhttp3.Interceptor
import okhttp3.Response

class APIInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val url = request.url.newBuilder().addQueryParameter("api_key", Constants.API_KEY).build()
        request = request.newBuilder().url(url).build()
        return chain.proceed(request)
    }
}