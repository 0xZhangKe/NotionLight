package com.zhangke.notionlib.data.block

import com.google.gson.annotations.SerializedName
import com.zhangke.notionlib.data.NotionFile
import com.zhangke.notionlib.data.RichText

data class CalloutBlock(

    @SerializedName("rich_text")
    val richText: List<RichText>,

    val color: String = "default",

    val icon: NotionFile,

    val children: List<ChildrenBlock>?
) : TypedBlock