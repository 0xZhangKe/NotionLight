package com.zhangke.notiontodo.setting

import android.content.pm.PackageManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.architect.daynight.DayNightHelper
import com.zhangke.architect.daynight.DayNightMode
import com.zhangke.framework.utils.appContext
import com.zhangke.notionlib.auth.NotionAuthorization
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
}