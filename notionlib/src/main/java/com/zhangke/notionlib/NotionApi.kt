package com.zhangke.notionlib

import com.zhangke.notionlib.auth.NotionTodoIntegrationConfig
import com.zhangke.notionlib.data.OauthToken
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Created by ZhangKe on 2022/3/13.
 */
interface NotionApi {

    @Headers("Authorization: ${NotionTodoIntegrationConfig.AUTHORIZATION}")
    @POST("/v1/oauth/token")
    suspend fun getOauthToken(@Body body: RequestBody): OauthToken
}