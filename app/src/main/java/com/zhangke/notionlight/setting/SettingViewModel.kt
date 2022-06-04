package com.zhangke.notionlight.setting

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
import com.zhangke.notionlib.data.OauthToken
import com.zhangke.notionlight.NotionLightConfig
import com.zhangke.notionlight.R
import com.zhangke.notionlight.config.NotionPageConfigRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingViewModel : ViewModel() {

    val userInfo = MutableLiveData<OauthToken?>(NotionAuthorization.getOauthToken())

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
        viewModelScope.launch {
            NotionAuthorization.loginStateFlow.collect {
                userInfo.value = NotionAuthorization.getOauthToken()
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

    fun logout() {
        NotionAuthorization.logout()
        viewModelScope.launch {
            NotionPageConfigRepo.nuke()
        }
    }

    fun feedbackByAppStore(activity: Activity) {
        openAppMarket(activity)
    }

    fun feedbackByEmail(activity: Activity) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "plain/text"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(NotionLightConfig.FEEDBACK_EMAIL))
            putExtra(Intent.EXTRA_SUBJECT, "NotionLight feedback")
        }
        activity.startActivity(intent)
    }

    fun feedbackByGithub(activity: Activity) {
        val uri = Uri.parse(NotionLightConfig.FEEDBACK_GITHUB)
        activity.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}