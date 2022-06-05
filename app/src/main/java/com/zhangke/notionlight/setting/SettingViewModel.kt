package com.zhangke.notionlight.setting

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.architect.daynight.DayNightHelper
import com.zhangke.architect.daynight.DayNightMode
import com.zhangke.architect.language.LanguageHelper
import com.zhangke.architect.language.LanguageSettingType
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

    fun getDayNightVm(mode: DayNightMode): DayNightSettingVm{
        return mode.toDayNightVm()
    }

    fun updateDayNight(vm: DayNightSettingVm) {
        DayNightHelper.setMode(vm.mode)
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

    fun getDayNightModeList(): List<DayNightSettingVm> {
        return listOf(
            DayNightMode.DAY.toDayNightVm(),
            DayNightMode.NIGHT.toDayNightVm(),
            DayNightMode.FOLLOW_SYSTEM.toDayNightVm(),
        )
    }

    private fun DayNightMode.toDayNightVm(): DayNightSettingVm {
        val name = when (this) {
            DayNightMode.DAY -> appContext.getString(R.string.setting_page_day_night_day)
            DayNightMode.NIGHT -> appContext.getString(R.string.setting_page_day_night_night)
            DayNightMode.FOLLOW_SYSTEM -> appContext.getString(R.string.setting_page_day_night_system)
        }
        return DayNightSettingVm(this, name)
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

    fun getSupportedLanguage(): List<LanguageSettingVm> {
        return listOf(
            LanguageSettingVm(
                LanguageSettingType.CN,
                appContext.getString(R.string.setting_language_zh)
            ),
            LanguageSettingVm(
                LanguageSettingType.EN,
                appContext.getString(R.string.setting_language_en)
            ),
            LanguageSettingVm(
                LanguageSettingType.SYSTEM,
                appContext.getString(R.string.setting_language_system)
            ),
        )
    }

    fun getCurrentLanguage(): LanguageSettingVm {
        val currentType = LanguageHelper.currentType
        return getSupportedLanguage().firstOrNull { it.type == currentType } ?: LanguageSettingVm(
            LanguageSettingType.SYSTEM,
            appContext.getString(R.string.setting_language_system)
        )
    }

    fun setLanguage(context: Context, language: LanguageSettingVm) {
        LanguageHelper.setLanguage(context, language.type)
    }

    data class LanguageSettingVm(
        val type: LanguageSettingType,
        val name: String
    )

    data class DayNightSettingVm(
        val mode: DayNightMode,
        val name: String
    )
}