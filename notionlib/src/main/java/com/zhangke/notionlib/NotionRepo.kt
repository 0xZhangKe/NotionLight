package com.zhangke.notionlib

import com.zhangke.architect.network.newRetrofit
import com.zhangke.framework.utils.*
import com.zhangke.notionlib.auth.NotionAuthorization
import com.zhangke.notionlib.data.*
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

    suspend fun requestOathToken(code: String): NotionResponse<OauthToken> {
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
            val response = queryPages(startCursor)
            response.onSuccess { data ->
                data.results?.let { list ->
                    pageList += list.filter { it.parent?.type != Parent.Type.DATABASE }
                }
                startCursor = data.nextCursor
                hasMore = data.hasMore
            }
        }
        return pageList
    }

    private suspend fun queryPages(startCursor: String? = null): NotionResponse<NotionListEntry<NotionPage>> {
        val json = json {
            "start_cursor" kvNotNull startCursor
            "page_size" kv 100
            "filter" kv json {
                "property" kv "object"
                "value" kv "page"
            }
        }.toString()
        val body = json.toRequestBody("application/json".toMediaType())
        return notionApi.queryAllPages(body)
    }

    suspend fun queryBlock(blockId: String, startCursor: String? = null): NotionResponse<NotionListEntry<NotionBlock>>{
        return notionApi.queryBlock(blockId, 100, startCursor)
    }
}