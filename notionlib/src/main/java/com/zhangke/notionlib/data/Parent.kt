package com.zhangke.notionlib.data

import com.google.gson.annotations.SerializedName
import com.zhangke.framework.utils.EnumWithJsonValue

data class Parent(

    @JvmField
    @SerializedName("type")
    val type: Type,

    @SerializedName("page_id")
    val pageId: String?,

    @SerializedName("database_id")
    val databaseId: String?,

    @SerializedName("workspace")
    val workspace: Boolean = true,
) {

    enum class Type(override val value: String) : EnumWithJsonValue<String> {

        DATABASE("database_id"),

        PAGE("page_id"),

        WORKSPACE("workspace")
    }
}
