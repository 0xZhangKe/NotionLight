package com.zhangke.notiontodo.setting

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.architect.daynight.DayNightHelper
import com.zhangke.architect.daynight.DayNightMode
import com.zhangke.framework.utils.appContext
import com.zhangke.framework.utils.toast
import com.zhangke.notionlib.auth.NotionAuthorization
import com.zhangke.notiontodo.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingViewModel : ViewModel() {

    val userInfo = NotionAuthorization.getOauthToken()

    val currentDayNightMode = MutableLiveData<DayNightMode>()

    val dayNightModeList = listOf(
        DayNightMode.DAY,
        DayNightMode.NIGHT,
        DayNightMode.FOLLOW_SYSTEM,
    )

    init {
        viewModelScope.launch {
            DayNightHelper.dayNightModeFlow
                .collect {
                    withContext(Dispatchers.Main) {
                        currentDayNightMode.value = it
                    }
                }
        }
    }

    fun getAppVersionDesc(): String {
        return try {
            val info = appContext.packageManager.getPackageInfo(appContext.packageName, 0)
            "${info.versionName}(${info.versionCode})"
        } catch (e: PackageManager.NameNotFoundException) {
            "unknown"
        }
    }

    fun updateDayNight(mode: DayNightMode) {
        DayNightHelper.setMode(mode)
    }

    fun openAppMarket(activity: Activity) {
        val uri = Uri.parse("market://details?id=${activity.packageName}")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            activity.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            toast(R.string.error_app_market_not_found)
        }
    }
}