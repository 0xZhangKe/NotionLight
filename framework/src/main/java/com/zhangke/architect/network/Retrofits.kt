package com.zhangke.architect.network

import com.zhangke.framework.utils.sharedGson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun newRetrofit(baseUrl: String): Retrofit =
    Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create(sharedGson))
        .addCallAdapterFactory(ResponseCallAdapterFactory())
        .client(GlobalOkHttpClient.client)
        .build()