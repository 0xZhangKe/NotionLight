package com.zhangke.notionlib.data

import com.google.gson.annotations.SerializedName
import com.zhangke.architect.network.ErrorResponse
import com.zhangke.framework.R
import com.zhangke.framework.utils.appContext

// {"object":"error","status":401,"code":"unauthorized","message":"API token is invalid."}
data class ErrorEntry(

    @SerializedName("object")
    val objectType: String,

    val status: Int,

    val code: String?,

    val message: String?,
) : ErrorResponse.ErrorEntryWithMessage {

    override val errorMessage: String
        get() = message ?: appContext.getString(R.string.unknown_error)
}
