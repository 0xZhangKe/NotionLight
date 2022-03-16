package com.zhangke.notionlib.data.block

import com.google.gson.annotations.SerializedName
import com.zhangke.notionlib.data.RichText

data class ParagraphBlock(

    @SerializedName("rich_text")
    val richText: List<RichText>,

    val color: String = "default",

    val children: List<ChildrenBlock>?
) : TypedBlock