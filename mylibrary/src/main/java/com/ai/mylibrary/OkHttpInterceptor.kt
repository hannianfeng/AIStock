package com.ai.mylibrary

import android.util.Log
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Response

class OkHttpInterceptor:Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val toString = originalRequest.body().toString()

//        val build = FormBody.Builder()
//            .add("code", captche)
//            .add("version", osVersion)
//            .add("model", deviceModel)
//            .add("ip", ipAddress.toString())
//            .add("userAgent", userAgent.toString())
//            .add("dev", deviceId)
//            .build()
        val request = originalRequest
            .newBuilder()
            .build()
        val body = request.body().toString()
        Log.e("---TAG---", "intercept-------: ${body[0]}" )

        return chain.proceed(request)
    }
}