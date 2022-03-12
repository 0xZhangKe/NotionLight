package com.zhangke.architect.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun newRetrofit(baseUrl: String): Retrofit =
    Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .client(GlobalOkHttpClient.client)
        .build()