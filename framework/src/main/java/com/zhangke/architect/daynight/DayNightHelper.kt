package com.zhangke.architect.daynight

import android.content.res.Configuration
import android.os.SystemClock
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.zhangke.architect.coroutines.ApplicationScope
import com.zhangke.architect.datastore.dataStore
import com.zhangke.architect.datastore.getInt
import com.zhangke.architect.datastore.putInt
import com.zhangke.framework.R
import com.zhangke.framework.utils.appContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object DayNightHelper {

    private const val DAY_NIGHT_SETTING = "day_night_setting"

    val dayNightModeFlow = MutableSharedFlow<DayNightMode>(replay = 1)

    private var dayNightMode: DayNightMode

    init {
        val modeValue = getDayNightModeFromLocal()
        AppCompatDelegate.setDefaultNightMode(modeValue)
        dayNightMode = modeValue.toDayNightMode()
        ApplicationScope.launch {
            dayNightModeFlow.emit(dayNightMode)
        }
    }

    fun setActivityDayNightMode() {
        AppCompatDelegate.setDefaultNightMode(dayNightMode.modeValue)
    }

    fun isNight(): Boolean {
        return dayNightMode.isNight()
    }

    private fun DayNightMode.isNight(): Boolean {
        return when (this) {
            DayNightMode.DAY -> false
            DayNightMode.NIGHT -> true
            DayNightMode.FOLLOW_SYSTEM -> systemIsNight()
        }
    }

    private fun systemIsNight(): Boolean {
        return appContext.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    fun setMode(mode: DayNightMode) {
        dayNightMode = mode
        ApplicationScope.launch {
            appContext.dataStore.putInt(DAY_NIGHT_SETTING, mode.modeValue)
        }
        AppCompatDelegate.setDefaultNightMode(mode.modeValue)
        ApplicationScope.launch {
            dayNightModeFlow.emit(mode)
        }
    }

    private fun getDayNightModeFromLocal(): Int {
        return runBlocking {
            appContext.dataStore.getInt(DAY_NIGHT_SETTING)
                ?: DayNightMode.FOLLOW_SYSTEM.modeValue
        }
    }

    private fun Int.toDayNightMode(): DayNightMode {
        return when (this) {
            DayNightMode.DAY.modeValue -> DayNightMode.DAY
            DayNightMode.NIGHT.modeValue -> DayNightMode.NIGHT
            DayNightMode.FOLLOW_SYSTEM.modeValue -> DayNightMode.FOLLOW_SYSTEM
            else -> throw IllegalArgumentException("Illegal $this for DayNightMode")
        }
    }
}

enum class DayNightMode(val modeValue: Int) {

    DAY(AppCompatDelegate.MODE_NIGHT_NO),

    NIGHT(AppCompatDelegate.MODE_NIGHT_YES),

    FOLLOW_SYSTEM(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
}