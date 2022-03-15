package com.zhangke.notionlib.data

import com.google.gson.annotations.SerializedName

data class NotionPage(

    @SerializedName("object")
    val objectType: String,

    val id: String,

    @SerializedName("created_time")
    val createdTime: String?,

    @SerializedName("created_by")
    val createdBy: User?,

    @SerializedName("last_edited_time")
    val lastEditedTime: String?,

    @SerializedName("last_edited_by")
    val lastEditedBy: User?,

    val archived: Boolean = false,

    /**
     * just emoji or external url currently
     */
    val icon: NotionFile?,

    /**
     * just external url currently
     */
    val cover: NotionFile?,

    val properties: TitleProperty?,

    val parent: Parent?,

    val url: String,
)
