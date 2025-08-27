package com.zhangke.notionlight

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.zhangke.architect.daynight.DayNightHelper
import com.zhangke.architect.language.LanguageHelper
import com.zhangke.framework.utils.initApplication
import com.zhangke.notionlib.NotionLibStartup
import com.zhangke.notionlib.auth.NotionAuthorization
import com.zhangke.notionlight.auth.AuthorizationActivity
import com.zhangke.notionlight.shorcut.AppShortcutManager

/**
 * Created by ZhangKe on 2022/3/13.
 */
class App : Application() {

    override fun onCreate() {
        Log.d("F_TEST", "App onCreate")
        super.onCreate()
        FirebaseApp.initializeApp(this)
        initApplication(this)
        DayNightHelper
        NotionLibStartup.onOpen()
        authStartup()
        AppShortcutManager
        LanguageHelper.prepare(this)
    }
}

private fun authStartup() {
    NotionAuthorization.onNeedShowAuthPage = {
        AuthorizationActivity.open()
    }
}