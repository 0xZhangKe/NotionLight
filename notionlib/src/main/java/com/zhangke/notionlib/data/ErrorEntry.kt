package com.zhangke.notionlib.data

import com.google.gson.annotations.SerializedName

// {"object":"error","status":401,"code":"unauthorized","message":"API token is invalid."}
data class ErrorEntry(

    @SerializedName("object")
    val objectType: String,

    val status: Int,

    val code: String?,

    val message: String?,
)
