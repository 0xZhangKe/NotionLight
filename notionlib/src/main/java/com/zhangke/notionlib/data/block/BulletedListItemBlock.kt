package com.zhangke.notionlib.data.block

import com.google.gson.annotations.SerializedName
import com.zhangke.notionlib.data.NotionFile
import com.zhangke.notionlib.data.RichText

data class BulletedListItemBlock(
    @SerializedName("rich_text")
    val richText: List<RichText>,

    val color: String = "default",

    val icon: NotionFile? = null,

    val children: List<ChildrenBlock>? = null
) : TypedBlock {

    companion object {
        const val TYPE = BlockType.BULLETED_LIST
    }
}
