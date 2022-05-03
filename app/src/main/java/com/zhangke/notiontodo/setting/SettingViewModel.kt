package com.zhangke.notiontodo.setting

import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import com.zhangke.framework.utils.appContext
import com.zhangke.notionlib.auth.NotionAuthorization

class SettingViewModel : ViewModel() {

    val userInfo = NotionAuthorization.getOauthToken()

    fun getAppVersionDesc(): String {
        return try {
            val info = appContext.packageManager.getPackageInfo(appContext.packageName, 0)
            "${info.versionName}(${info.versionCode})"
        } catch (e: PackageManager.NameNotFoundException) {
            "unknown"
        }
    }
}