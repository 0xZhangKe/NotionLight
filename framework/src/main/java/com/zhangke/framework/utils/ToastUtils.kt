package com.zhangke.framework.utils

import android.widget.Toast
import androidx.annotation.StringRes

fun toast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(appContext, message, length).show()
}

fun toast(@StringRes resId: Int, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(appContext, appContext.getString(resId), length).show()
}