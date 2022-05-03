package com.zhangke.notionlib.ext

import com.zhangke.notionlib.data.block.*

fun TypedBlock.getSimpleText(): String? {
    return when (this) {
        is CalloutBlock -> richText.getSimpleText()
        is HeadingBlock -> richText.getSimpleText()
        is ParagraphBlock -> richText.getSimpleText()
        is QuoteBlock -> richText.getSimpleText()
        is TodoBlock -> richText.getSimpleText()
        else -> null
    }
}