package com.zhangke.framework.utils

import android.content.res.Resources

fun Float.toDp(): Int {
    val scale: Float = Resources.getSystem().displayMetrics.density
    return (this / scale + 0.5f).toInt()
}

fun Int.toDp(): Int {
    val scale: Float = Resources.getSystem().displayMetrics.density
    return this.toFloat().toDp()
}