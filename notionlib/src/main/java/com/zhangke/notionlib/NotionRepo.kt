package com.zhangke.notionlib

import com.zhangke.architect.network.newRetrofit
import com.zhangke.notionlib.data.OauthToken

object NotionRepo {

    private val notionApi: NotionApi by lazy {
        newRetrofit("https://api.notion.com").create(
            NotionApi::class.java
        )
    }

    suspend fun getOauthToken(authorization: String): OauthToken {
        return notionApi.getOauthToken("Basic $authorization")
    }
}