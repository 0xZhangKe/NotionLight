package com.zhangke.notiontodo.daynight

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.zhangke.architect.coroutines.ApplicationScope
import com.zhangke.architect.datastore.dataStore
import com.zhangke.architect.datastore.getInt
import com.zhangke.architect.datastore.putInt
import com.zhangke.framework.utils.appContext
import kotlinx.coroutines.launch


object DayNightHelper {

    private const val DAY_NIGHT_SETTING = "day_night_setting"

    private var isNight = false

    init {
        ApplicationScope.launch {
            val dayNightMode = getCurrentDayNightMode()
//            isNight =
            AppCompatDelegate.setDefaultNightMode(dayNightMode)
        }
    }

    suspend fun getMode(): DayNightMode {
        return when (getCurrentDayNightMode()) {
            DayNightMode.DAY.modeValue -> DayNightMode.DAY
            DayNightMode.NIGHT.modeValue -> DayNightMode.NIGHT
            DayNightMode.FOLLOW_SYSTEM.modeValue -> DayNightMode.FOLLOW_SYSTEM
            else -> DayNightMode.FOLLOW_SYSTEM
        }
    }

    fun isNight(): Boolean {
        return (appContext.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_YES) != 0
    }

    fun setMode(mode: DayNightMode) {
        ApplicationScope.launch {
            appContext.dataStore.putInt(DAY_NIGHT_SETTING, mode.modeValue)
        }
        AppCompatDelegate.setDefaultNightMode(mode.modeValue)
    }

    private suspend fun getCurrentDayNightMode(): Int {
        return appContext.dataStore.getInt(DAY_NIGHT_SETTING)
            ?: DayNightMode.FOLLOW_SYSTEM.modeValue
    }
}

enum class DayNightMode(val modeValue: Int) {

    DAY(AppCompatDelegate.MODE_NIGHT_NO),

    NIGHT(AppCompatDelegate.MODE_NIGHT_YES),

    FOLLOW_SYSTEM(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
}