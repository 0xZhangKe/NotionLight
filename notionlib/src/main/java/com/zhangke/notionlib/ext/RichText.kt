package com.zhangke.notionlib.ext

import com.zhangke.notionlib.data.RichText

/**
 * ignore all label
 */
fun List<RichText>.getSimpleText(): String? {
    return firstOrNull()?.plainText
}