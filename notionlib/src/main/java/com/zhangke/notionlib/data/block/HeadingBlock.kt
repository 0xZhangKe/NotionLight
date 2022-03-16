package com.zhangke.notionlib.data.block

import com.google.gson.annotations.SerializedName
import com.zhangke.notionlib.data.RichText

data class HeadingBlock(

    @SerializedName("rich_text")
    val richText: List<RichText>,

    val color: String = "default"
): TypedBlock