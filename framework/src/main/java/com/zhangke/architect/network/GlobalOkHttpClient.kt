package com.zhangke.architect.network

import com.zhangke.framework.utils.ifDebugging
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object GlobalOkHttpClient {

    private const val TIMEOUT = 30L

    val client: OkHttpClient = createBuilder().build()

    private fun createBuilder(): OkHttpClient.Builder {
        val builder = OkHttpClient().newBuilder()
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
        ifDebugging {
            builder.addInterceptor(
                HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT)
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
        }
        return builder
    }
}
