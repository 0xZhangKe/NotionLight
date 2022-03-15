package com.zhangke.notionlib.data

import com.google.gson.annotations.SerializedName

data class NotionListEntry<T>(

    @SerializedName("object")
    val objectType: String,

    val type: String?,

    @SerializedName("has_more")
    val hasMore: Boolean,

    @SerializedName("next_cursor")
    val nextCursor: String?,

    val results: List<T>?,
)