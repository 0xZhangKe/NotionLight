package com.zhangke.framework.utils

import android.view.Gravity
import android.widget.Toast
import androidx.annotation.StringRes

fun toast(message: String, length: Int = Toast.LENGTH_SHORT, gravity: Int = Gravity.BOTTOM) {
    val toast = Toast.makeText(appContext, message, length)
    toast.setGravity(gravity, 0, 0)
    toast.show()
}

fun toast(@StringRes resId: Int, length: Int = Toast.LENGTH_SHORT, gravity: Int = Gravity.BOTTOM) {
    toast(appContext.getString(resId), length, gravity)
}
