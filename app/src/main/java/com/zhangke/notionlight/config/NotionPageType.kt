package com.zhangke.notionlight.config

enum class NotionPageType(val value: String) {

    TODO("todo"),

    PARAGRAPH("paragraph"),

    CALLOUT("callout"),

    ;

    companion object {

        val all = listOf(TODO, PARAGRAPH, CALLOUT)
    }
}