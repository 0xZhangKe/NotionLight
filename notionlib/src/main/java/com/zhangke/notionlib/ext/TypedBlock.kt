package com.zhangke.notionlib.ext

import com.zhangke.notionlib.data.NotionBlock
import com.zhangke.notionlib.data.block.*

fun NotionBlock.getLightText(): String?{
    return childrenBlock?.getLightText()
}

fun TypedBlock.getLightText(): String? {
    return when (this) {
        is CalloutBlock -> richText.getSimpleText()
        is HeadingBlock -> richText.getSimpleText()
        is ParagraphBlock -> richText.getSimpleText()
        is QuoteBlock -> richText.getSimpleText()
        is TodoBlock -> richText.getSimpleText()
        is BulletedListItemBlock -> richText.getSimpleText()
        is NumberListItemBlock -> richText.getSimpleText()
        else -> null
    }
}