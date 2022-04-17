package com.zhangke.framework.utils

class Optional<T> private constructor(private val value: T? = null) {

    companion object {

        private val EMPTY = Optional<Any>()

        fun <T> empty(): Optional<T> {
            @Suppress("UNCHECKED_CAST")
            return EMPTY as Optional<T>
        }

        fun <T> of(value: T?): Optional<T> {
            return Optional(value)
        }
    }

    fun get(): T = value!!

    fun getOrNull(): T? = value

    fun isPresent(): Boolean = value != null
}