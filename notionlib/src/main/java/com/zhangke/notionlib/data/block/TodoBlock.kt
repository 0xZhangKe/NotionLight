package com.zhangke.notionlib.data.block

import com.google.gson.annotations.SerializedName
import com.zhangke.notionlib.data.RichText

data class TodoBlock(

    @SerializedName("rich_text")
    val richText: List<RichText>,

    val color: String = "default",

    val checked: Boolean,

    val children: List<ChildrenBlock>?
) : TypedBlock {

    companion object {
        const val TYPE = BlockType.TODO
    }
}