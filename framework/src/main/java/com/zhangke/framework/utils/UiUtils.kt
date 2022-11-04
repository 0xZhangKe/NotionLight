package com.zhangke.framework.utils

import android.content.res.Resources

val Number.toDp: Float
    get() {
        val scale: Float = Resources.getSystem().displayMetrics.density
        return toFloat() / scale
    }
