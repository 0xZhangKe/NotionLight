package com.zhangke.notionlib.data

import com.google.gson.annotations.SerializedName
import com.zhangke.framework.utils.EnumWithJsonValue

data class NotionFile(

    @JvmField
    @SerializedName("type")
    val type: Type,

    val emoji: String?,

    val file: UploadedFile?,

    val external: External?
) {

    data class External(val url: String)

    data class UploadedFile(
        val url: String,

        @SerializedName("expiry_time")
        val expiryTime: String
    )

    enum class Type(override val value: String) : EnumWithJsonValue<String> {

        EMOJI("emoji"),

        FILE("file"),

        EXTERNAL("external")
    }
}
