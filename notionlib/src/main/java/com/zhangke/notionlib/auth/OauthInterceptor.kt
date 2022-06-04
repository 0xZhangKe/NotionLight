package com.zhangke.notionlib.auth

import android.util.Log
import com.zhangke.framework.utils.Optional
import com.zhangke.framework.utils.sharedGson
import com.zhangke.notionlib.NotionApi
import com.zhangke.notionlib.data.ErrorEntry
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class OauthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = if (request.header(NotionApi.OAUTH_HEADER_TAG) == null) {
            val token = requireAccessToken()
            request.newBuilder()
                .addHeader("Authorization", token)
                .build()
        } else {
            request.newBuilder()
                .removeHeader(NotionApi.OAUTH_HEADER_TAG)
                .build()
        }
        val response = chain.proceed(request)
        if (response.code == 401) {
            var errorEntry: ErrorEntry? = null
            try {
                val body = response.body!!
                val source = body.source().buffer.clone()
                errorEntry =
                    sharedGson.fromJson(source.readString(Charsets.UTF_8), ErrorEntry::class.java)
            } catch (e: Exception) {
                Log.e("OauthInterceptor", "intercept", e)
            }
            NotionAuthorization.showAuthPage()
            throw NotOauthException(errorEntry)
        }
        return response
    }

    private fun requireAccessToken(): String {
        val token = NotionAuthorization.readTokenSubject
            .map { Optional.of(NotionAuthorization.getOauthToken()) }
            .blockingGet()
            .getOrNull()
            ?.accessToken
        if (token.isNullOrEmpty()) {
            NotionAuthorization.showAuthPage()
            throw NotOauthException(null)
        }
        return "Bearer $token"
    }
}

class NotOauthException(errorEntry: ErrorEntry?) : IOException(errorEntry?.message)