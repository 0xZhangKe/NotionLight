package com.zhangke.framework.utils

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object StatusBarUtils {

    fun getStatusBarHeight(): Dp {
        val resId = appContext.resources.getIdentifier("status_bar_height", "deimen", "android")
        return if (resId > 0) {
            appContext.resources.getDimension(resId).dp
        } else {
            0.dp
        }
    }
}