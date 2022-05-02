package com.zhangke.framework.utils

data class DataWithLoading<T>(
    val data: T? = null,
    val state: LoadingState,
    val exception: Throwable? = null,
) {

    companion object {

        fun <T> idle(data: T? = null): DataWithLoading<T> {
            return DataWithLoading(data, LoadingState.IDLE)
        }

        fun <T> loading(data: T? = null): DataWithLoading<T> {
            return DataWithLoading(data, LoadingState.LOADING)
        }

        fun <T> success(data: T): DataWithLoading<T> {
            return DataWithLoading(data, LoadingState.SUCCESS)
        }

        fun <T> failed(data: T? = null, exception: Throwable? = null): DataWithLoading<T> {
            return DataWithLoading(data, LoadingState.FAILED, exception)
        }
    }
}

enum class LoadingState {

    IDLE,

    LOADING,

    SUCCESS,

    FAILED
}
