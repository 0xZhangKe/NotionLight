package com.zhangke.framework.utils

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object StatusBarUtils {

    fun getStatusBarHeight(): Dp {
        val resId = appContext.resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resId > 0) {
            appContext.resources.getDimensionPixelSize(resId).toDp.dp
        } else {
            0.dp
        }
    }
}