package com.zhangke.architect.network

import okhttp3.Request
import okhttp3.ResponseBody
import okio.Timeout
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ResponseCallAdapterFactory : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        // Kotlin suspend function will wrap result with retrofit.Call
        if (Call::class.java != getRawType(returnType)) return null
        if (returnType !is ParameterizedType) {
            return null
        }
        val responseType = getParameterUpperBound(0, returnType)
        val responseRawType = getRawType(responseType)
        if (!Response::class.java.isAssignableFrom(responseRawType)) {
            return null
        }
        if (responseType !is ParameterizedType) return null
        @Suppress("UNCHECKED_CAST")
        val responseInstance =
            getRawType(responseType).newInstance() as Response<*, ErrorResponse.ErrorEntryWithMessage>
        val successType = getParameterUpperBound(0, responseType)
        val errorType =
            if (responseRawType == Response::class.java) {
                fetchErrorTypeFromResponse(responseType)
            } else {
                fetchErrorType(responseType) ?: return null
            }
        if (!ErrorResponse.ErrorEntryWithMessage::class.java.isAssignableFrom(getRawType(errorType))) {
            return null
        }
        val errorBodyConverter: Converter<ResponseBody, ErrorResponse.ErrorEntryWithMessage> =
            retrofit.nextResponseBodyConverter(null, errorType, annotations)
        return ResponseCallAdapter(
            responseInstance,
            successType,
            errorBodyConverter
        )
    }

    private fun fetchErrorTypeFromResponse(responseType: ParameterizedType): Type {
        return getParameterUpperBound(1, responseType)
    }

    private fun fetchErrorType(responseType: Type): Type? {
        return try {
            val superClass = responseType.getResponseType() as? ParameterizedType ?: return null
            val errorType = getRawType(getParameterUpperBound(1, superClass))
            if (ErrorResponse.ErrorEntryWithMessage::class.java.isAssignableFrom(errorType)) {
                errorType
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun Type.getResponseType(): Type? {
        if (Response::class.java == getRawType(this)) return this
        val supperClass =
            try {
                getRawType(this).genericSuperclass
            } catch (e: IllegalArgumentException) {
                null
            }
        if (supperClass != null) {
            return supperClass.getResponseType()
        }
        return null
    }
}

class ResponseCallAdapter<S, E : ErrorResponse.ErrorEntryWithMessage>(
    private val responseInstance: Response<S, E>,
    private val successType: Type,
    private val errorConverter: Converter<ResponseBody, E>
) : CallAdapter<S, Call<Response<S, E>>> {

    override fun responseType(): Type = successType

    override fun adapt(call: Call<S>): Call<Response<S, E>> {
        return ResponseCall(call, responseInstance, errorConverter)
    }
}

class ResponseCall<S, E : ErrorResponse.ErrorEntryWithMessage>(
    private val delegate: Call<S>,
    private val responseInstance: Response<S, E>,
    private val errorConverter: Converter<ResponseBody, E>
) : Call<Response<S, E>> {

    override fun enqueue(callback: Callback<Response<S, E>>) {
        return delegate.enqueue(object : Callback<S> {

            override fun onResponse(call: Call<S>, response: retrofit2.Response<S>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    responseInstance.successData = body
                } else {
                    val error = response.errorBody()
                    val errorBody = when {
                        error == null -> null
                        error.contentLength() == 0L -> null
                        else -> {
                            try {
                                errorConverter.convert(error)
                            } catch (e: Exception) {
                                null
                            }
                        }
                    }
                    responseInstance.errorData = ErrorResponse.ApiError(errorBody, response.code())
                }
                callback.onResponse(this@ResponseCall, retrofit2.Response.success(responseInstance))
            }

            override fun onFailure(p0: Call<S>, p1: Throwable) {
                responseInstance.errorData = ErrorResponse.NetworkError(p1)
                callback.onResponse(this@ResponseCall, retrofit2.Response.success(responseInstance))
            }
        })
    }

    override fun isExecuted(): Boolean = delegate.isExecuted

    override fun clone(): Call<Response<S, E>> {
        return ResponseCall(delegate.clone(), responseInstance, errorConverter)
    }

    override fun cancel() {
        delegate.cancel()
    }

    override fun isCanceled(): Boolean = delegate.isCanceled

    override fun execute(): retrofit2.Response<Response<S, E>> {
        throw UnsupportedOperationException("ResponseCall not support execute")
    }

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()
}