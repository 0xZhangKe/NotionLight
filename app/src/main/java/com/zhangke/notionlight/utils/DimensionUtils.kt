package com.zhangke.notionlight.utils

import android.util.TypedValue
import com.zhangke.framework.utils.appContext

/**
 * Created by ZhangKe on 2022/10/29.
 */

val Number.toPx
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        appContext.resources.displayMetrics
    )