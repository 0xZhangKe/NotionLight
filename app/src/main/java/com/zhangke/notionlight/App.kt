package com.zhangke.notionlight

import androidx.multidex.MultiDexApplication
import com.zhangke.framework.utils.initApplication
import com.zhangke.notionlib.NotionLibStartup
import com.zhangke.architect.daynight.DayNightHelper
import com.zhangke.notionlib.auth.NotionAuthorization
import com.zhangke.notionlight.auth.AuthorizationActivity
import com.zhangke.notionlight.shorcut.AppShortcutManager

/**
 * Created by ZhangKe on 2022/3/13.
 */
class App: MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        initApplication(this)
        DayNightHelper
        NotionLibStartup.onOpen()
        authStartup()
        AppShortcutManager
    }
}

private fun authStartup(){
    NotionAuthorization.onNeedShowAuthPage = {
        AuthorizationActivity.open()
    }
}