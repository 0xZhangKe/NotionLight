package com.zhangke.notionlib

import com.zhangke.architect.network.GlobalOkHttpClient
import com.zhangke.notionlib.auth.NotionAuthorization
import com.zhangke.notionlib.auth.OauthInterceptor

object NotionLibStartup {

    fun onOpen() {
        GlobalOkHttpClient.addThirdPartInterceptor(OauthInterceptor())
        NotionAuthorization.start()
    }
}