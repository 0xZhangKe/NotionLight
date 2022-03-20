package com.zhangke.architect.network

import com.zhangke.framework.R
import com.zhangke.framework.utils.appContext

sealed class ErrorResponse<T : ErrorResponse.ErrorEntryWithMessage>(val message: String) {

    data class ApiError<T : ErrorEntryWithMessage>(val data: T?, val code: Int) :
        ErrorResponse<T>(data?.errorMessage ?: appContext.getString(R.string.unknown_error))

    data class NetworkError<T : ErrorEntryWithMessage>(val error: Throwable) :
        ErrorResponse<T>(appContext.getString(R.string.network_error))

    interface ErrorEntryWithMessage {
        val errorMessage: String
    }
}