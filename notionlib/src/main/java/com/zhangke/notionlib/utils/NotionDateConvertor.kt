package com.zhangke.notionlib.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object NotionDateConvertor {

    private val notionDateFormatThreadLocal = ThreadLocal<DateFormat>()

    fun convert(time: String?): Date? {
        time ?: return null
        return requireDateFormat()
            .runCatching { parse(time) }
            .getOrNull()
    }

    private fun requireDateFormat(): DateFormat {
        var format = notionDateFormatThreadLocal.get()
        if (format == null) {
            format = createNotionDateFormat()
            notionDateFormatThreadLocal.set(format)
        }
        return format
    }

    private fun createNotionDateFormat(): DateFormat {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ROOT)
    }
}