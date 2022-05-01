package com.zhangke.notionlib.data

import com.google.gson.annotations.SerializedName
import com.zhangke.framework.utils.JsonEnum

data class RichText(

    @SerializedName("plain_text")
    val plainText: String?,

    val href: String? = null,

    val annotations: TextAnnotations,

    @JvmField
    @SerializedName("type")
    val type: Type = Type.TEXT,

    val text: Text? = null,
) {

    enum class Type(override val value: String) : JsonEnum<String> {

        TEXT("text"),

        MENTION("mention"),

        EQUATION("equation")
    }

    data class Text(
        val content: String,
        val link: Link? = null,
    )

    data class Link(
        val type: String = "url",
        val url: String,
    )
}
