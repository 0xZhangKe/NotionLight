package com.zhangke.notionlib

import com.zhangke.architect.network.newRetrofit
import com.zhangke.framework.utils.*
import com.zhangke.notionlib.auth.NotionAuthorization
import com.zhangke.notionlib.data.NotionListEntry
import com.zhangke.notionlib.data.NotionPage
import com.zhangke.notionlib.data.OauthToken
import com.zhangke.notionlib.data.Parent
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

object NotionRepo {

    private const val OAUTH_TOKEN_KEY = "oauth_token"

    private val notionApi: NotionApi by lazy {
        newRetrofit("https://api.notion.com").create(
            NotionApi::class.java
        )
    }

    suspend fun getLocalOauthToken(): OauthToken? {
        val oauthTokenJson = appContext.dataStore.getString(OAUTH_TOKEN_KEY) ?: return null
        return sharedGson.fromJson(oauthTokenJson, OauthToken::class.java)
    }

    suspend fun saveOauthToken(token: OauthToken) {
        val json = sharedGson.toJson(token)
        appContext.dataStore.putString(OAUTH_TOKEN_KEY, json)
    }

    suspend fun requestOathToken(code: String): OauthToken {
        val json = json {
            "grant_type" kv "authorization_code"
            "code" kv code
            "redirect_uri" kv NotionAuthorization.REDIRECT_URL
        }.toString()
        val body = json.toRequestBody("application/json".toMediaType())
        return notionApi.getOauthToken(body)
    }

    suspend fun queryAllPages(): List<NotionPage> {
        var hasMore = true
        var startCursor: String? = null
        val pageList = mutableListOf<NotionPage>()
        while (hasMore) {
            val entry = queryPages(startCursor)
            entry.results?.let { list ->
                pageList += list.filter { it.parent?.type != Parent.Type.DATABASE }
            }
            startCursor = entry.nextCursor
            hasMore = entry.hasMore
        }
        return pageList
    }

    private suspend fun queryPages(startCursor: String? = null): NotionListEntry<NotionPage> {
        val json = json {
            "start_cursor" kvNotNull startCursor
            "page_size" kv 100
            "filter" kv json {
                "property" kv "object"
                "value" kv "page"
            }
        }.toString()
        val body = json.toRequestBody("application/json".toMediaType())
        return notionApi.queryAllPages(requireAccessToken(), body)
    }

    private suspend fun requireAccessToken(): String {
        val token = NotionAuthorization.getOauthToken()?.accessToken
        if (token.isNullOrEmpty()) {
            NotionAuthorization.startAuth()
            throw NoOauthException()
        }
        return "Bearer $token"
    }
}