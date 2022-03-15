package com.zhangke.notionlib.data

data class TextAnnotations(
    val bold: Boolean,
    val italic: Boolean,
    val strikethrough: Boolean,
    val code: Boolean,
    val color: String
)