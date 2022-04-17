package com.zhangke.notionlib

import com.zhangke.notionlib.auth.NotionTodoIntegrationConfig
import com.zhangke.notionlib.data.NotionBlock
import com.zhangke.notionlib.data.NotionListEntry
import com.zhangke.notionlib.data.NotionPage
import com.zhangke.notionlib.data.OauthToken
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * Created by ZhangKe on 2022/3/13.
 */
interface NotionApi {

    companion object {

        const val OAUTH_HEADER_TAG = "no_verification_tag_z"
    }

    @Headers(
        "Authorization: ${NotionTodoIntegrationConfig.AUTHORIZATION}",
        "$OAUTH_HEADER_TAG: true"
    )
    @POST("/v1/oauth/token")
    suspend fun getOauthToken(@Body body: RequestBody): NotionResponse<OauthToken>

    @Headers("Accept: application/json", "Notion-Version: 2022-02-22")
    @POST("v1/search")
    suspend fun queryAllPages(@Body body: RequestBody): NotionResponse<NotionListEntry<NotionPage>>

    @Headers("Accept: application/json", "Notion-Version: 2022-02-22")
    @GET("v1/blocks/{block_id}/children")
    suspend fun queryBlock(
        @Path("block_id") blockId: String,
        @Query("page_size") pageSize: Int,
        @Query("start_cursor") startCursor: String?
    ): NotionResponse<NotionListEntry<NotionBlock>>

    @PATCH("v1/blocks/{block_id}/children")
    suspend fun appendBlock(@Body body: RequestBody): NotionResponse<NotionListEntry<NotionBlock>>
}