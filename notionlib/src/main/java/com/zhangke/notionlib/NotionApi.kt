package com.zhangke.notionlib

import com.zhangke.notionlib.data.OauthToken
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Created by ZhangKe on 2022/3/13.
 */
interface NotionApi {

    @Headers("Content-Type: application/json")
    @POST("/v1/oauth/token")
    suspend fun getOauthToken(@Header("Authorization") authorization: String): OauthToken
}