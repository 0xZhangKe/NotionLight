package com.zhangke.notionlib

import com.google.gson.JsonObject
import com.zhangke.architect.network.newRetrofit
import com.zhangke.notionlib.auth.NotionAuthorization
import com.zhangke.notionlib.data.NotionBlock
import com.zhangke.notionlib.data.NotionListEntry
import com.zhangke.notionlib.data.NotionPage
import com.zhangke.notionlib.data.OauthToken
import com.zhangke.notionlib.utils.BlockBuildHelper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

object NotionRepo {

    private val notionApi: NotionApi by lazy {
        newRetrofit("https://api.notion.com").create(
            NotionApi::class.java
        )
    }

    suspend fun requestOathToken(code: String): NotionResponse<OauthToken> {
        val json = JsonObject().apply {
            addProperty("grant_type", "authorization_code")
            addProperty("code", code)
            addProperty("redirect_uri", NotionAuthorization.REDIRECT_URL)
        }.toString()
        val body = json.toRequestBody("application/json".toMediaType())
        return notionApi.getOauthToken(body)
    }

    suspend fun appendBlock(
        content: String,
        pageId: String,
        type: String
    ): NotionResponse<NotionListEntry<NotionBlock>> {
        val json = BlockBuildHelper.build(type, content).toString()
        val body = json.toRequestBody("application/json".toMediaType())
        return notionApi.appendBlock(pageId, body)
    }

    suspend fun queryAllPages(): List<NotionPage> {
        return loadAllPage { queryPages(it) }
    }

    private suspend fun queryPages(startCursor: String? = null): NotionResponse<NotionListEntry<NotionPage>> {
        val json = JsonObject().apply {
            addProperty("start_cursor", startCursor)
            addProperty("page_size", 100)
            val filter = JsonObject().apply {
                addProperty("property", "object")
                addProperty("value", "page")
            }
            add("filter", filter)
        }.toString()
        val body = json.toRequestBody("application/json".toMediaType())
        return notionApi.queryAllPages(body)
    }

    suspend fun queryAllBlocks(blockId: String): List<NotionBlock> {
        return loadAllPage { startCursor ->
            queryBlock(blockId, startCursor)
        }
    }

    suspend fun queryBlock(
        blockId: String,
        startCursor: String? = null
    ): NotionResponse<NotionListEntry<NotionBlock>> {
        return notionApi.queryBlock(blockId, 100, startCursor)
    }

    private inline fun <T> loadAllPage(loader: (String?) -> NotionResponse<NotionListEntry<T>>): List<T> {
        var hasMore = true
        var startCursor: String? = null
        val pageList = mutableListOf<T>()
        while (hasMore) {
            val response = loader(startCursor)
            response.onSuccess { data ->
                data.results?.let { list ->
                    pageList.addAll(list)
                }
                startCursor = data.nextCursor
                hasMore = data.hasMore
            }
            response.onError {
                hasMore = false
            }
        }
        return pageList
    }
}