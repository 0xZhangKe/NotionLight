package com.zhangke.notionlib

import com.zhangke.notionlib.auth.NotionTodoIntegrationConfig
import com.zhangke.notionlib.data.NotionListEntry
import com.zhangke.notionlib.data.NotionPage
import com.zhangke.notionlib.data.OauthToken
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * Created by ZhangKe on 2022/3/13.
 */
interface NotionApi {

    @Headers("Authorization: ${NotionTodoIntegrationConfig.AUTHORIZATION}")
    @POST("/v1/oauth/token")
    suspend fun getOauthToken(@Body body: RequestBody): OauthToken

    @Headers("Accept: application/json", "Notion-Version: 2022-02-22")
    @POST("v1/search")
    suspend fun queryAllPages(
        @Header("Authorization") authorization: String,
        @Body body: RequestBody
    ): NotionListEntry<NotionPage>
}