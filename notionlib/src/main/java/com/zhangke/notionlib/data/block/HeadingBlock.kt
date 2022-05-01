package com.zhangke.notionlib.data.block

import com.google.gson.annotations.SerializedName
import com.zhangke.notionlib.data.RichText

data class HeadingBlock(

    @SerializedName("rich_text")
    val richText: List<RichText>,

    val color: String = "default"
) : TypedBlock {

    companion object {

        const val TYPE_1 = BlockType.HEADING_1

        const val TYPE_2 = BlockType.HEADING_2

        const val TYPE_3 = BlockType.HEADING_3
    }
}