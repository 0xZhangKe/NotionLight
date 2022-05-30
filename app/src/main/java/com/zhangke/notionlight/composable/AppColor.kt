package com.zhangke.notionlight.composable

import androidx.compose.ui.graphics.Color
import com.zhangke.architect.daynight.DayNightHelper

object AppColor {

    private const val TRANSLUCENT_BACKGROUND = 0xAA000000

    val translucentBackground: Color
        get() =
            if (DayNightHelper.isNight()) {
                Color.Transparent
            } else {
                Color(TRANSLUCENT_BACKGROUND)
            }
}