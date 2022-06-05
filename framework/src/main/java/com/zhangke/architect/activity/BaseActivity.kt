package com.zhangke.architect.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhangke.architect.daynight.DayNightHelper
import com.zhangke.architect.language.LanguageHelper

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        DayNightHelper.setActivityDayNightMode()
        super.onCreate(savedInstanceState)
    }

    override fun onNightModeChanged(mode: Int) {
        super.onNightModeChanged(mode)
        recreate()
    }
}