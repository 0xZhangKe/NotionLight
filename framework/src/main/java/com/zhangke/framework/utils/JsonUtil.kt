package com.zhangke.framework.utils

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

val sharedGson: Gson = Gson()

/**
 * 解析[this]为[type]类型。非法值会导致[JsonSyntaxException]。
 * 空字符串为非法值。
 *
 * @throws JsonSyntaxException 如果[this]无法被解析为[type]。
 * @throws ClassCastException 如果解析出的对象不是[T]类型的对象。
 */
fun <T> String.parseJson(type: Type): T {
    if (this.isBlank()) {
        // 由于[历史原因](https://github.com/google/gson/issues/540)，Gson对空字符串返回null值。这对Kotlin的nullability是有害的。
        throw JsonSyntaxException("Empty input not allowed.")
    }
    return sharedGson.fromJson(this, type)
}

fun <T> JsonElement.parseJson(type: Type): T {
    return sharedGson.fromJson(this, type)
}

/**
 * 解析[this]为[T]类型。非法值会导致[JsonSyntaxException]。
 * 空字符串为非法值。
 *
 * 因为泛型擦除的缘故，[T]不能是泛型类。
 * 如果T继承了泛型类但本身没有泛型参数，或者T拥有泛型的字段，这两种情况不认为T是泛型类。
 *
 * @throws JsonSyntaxException 如果[this]无法被解析为[T]。
 */
inline fun <reified T> String.parseJson(): T {
    return parseJson(T::class.java)
}

/**
 * 解析[this]为[T]类型。非法值会导致[JsonSyntaxException]。
 * 空字符串为非法值。
 *
 * 因为泛型擦除的缘故，[T]不能是泛型类。
 * 如果T继承了泛型类但本身没有泛型参数，或者T拥有泛型的字段，这两种情况不认为T是泛型类。
 *
 * @throws JsonSyntaxException 如果[this]无法被解析为[T]。
 */
inline fun <reified T> JsonElement.parseJson(): T {
    return parseJson(T::class.java)
}

/**
 * 解析[this]为[T]类型。非法值会导致[JsonSyntaxException]。
 * 空字符串为非法值。
 *
 * 可以接受泛型的类型参数。会对每处调用创建一个新的匿名类，因此不应当用于非泛型的类型。
 *
 * @throws JsonSyntaxException 如果[this]无法被解析为[T]。
 */
inline fun <reified T> String.parseJsonGeneric(): T {
    return parseJson(object : TypeToken<T>() {}.type)
}

/**
 * 解析[this]为[T]类型。非法值会导致[JsonSyntaxException]。
 * 空字符串为非法值。
 *
 * 可以接受泛型的类型参数。会对每处调用创建一个新的匿名类，因此不应当用于非泛型的类型。
 *
 * @throws JsonSyntaxException 如果[this]无法被解析为[T]。
 */
inline fun <reified T> JsonElement.parseJsonGeneric(): T {
    return parseJson(object : TypeToken<T>() {}.type)
}

fun Any?.toJson(): String = sharedGson.toJson(this)

fun Any?.toJsonTree(): JsonElement = sharedGson.toJsonTree(this)

operator fun JsonObject.set(key: String, value: String?) {
    addProperty(key, value)
}

operator fun JsonObject.set(key: String, value: Boolean?) {
    addProperty(key, value)
}

operator fun JsonObject.set(key: String, value: Number?) {
    addProperty(key, value)
}

operator fun JsonObject.set(key: String, value: Char?) {
    addProperty(key, value)
}

operator fun JsonObject.set(key: String, value: JsonElement?) {
    add(key, value)
}

/**
 * 构建一个[JsonObject]。在[buildBlock]中，使用[JsonObjectBuilder.kv]创建键值对。
 */
inline fun json(buildBlock: JsonObjectBuilder2.() -> Unit): JsonObject {
    val builder = JsonObjectBuilder2()
    builder.buildBlock()
    return builder.json
}

inline fun <T> Iterable<T>.toJsonArray(transform: (T) -> JsonElement): JsonArray {
    val result = JsonArray()
    for (t in this) {
        result.add(transform(t))
    }
    return result
}

inline fun <T> Iterable<T>.toJsonObject(transform: (T) -> Pair<String, JsonElement>): JsonObject {
    val result = JsonObject()
    for (t in this) {
        val transformed = transform(t)
        result.add(transformed.first, transformed.second)
    }
    return result
}

@JvmInline
value class JsonObjectBuilder2(val json: JsonObject = JsonObject()) {
    infix fun String.kv(value: JsonElement?) {
        json.add(this, value)
    }

    infix fun String.kv(value: String?) {
        json.addProperty(this, value)
    }

    infix fun String.kv(value: Boolean?) {
        json.addProperty(this, value)
    }

    infix fun String.kv(value: Number?) {
        json.addProperty(this, value)
    }

    infix fun String.kv(value: Char?) {
        json.addProperty(this, value)
    }

    infix fun String.kvNotNull(value: JsonElement?) {
        if (value != null) this kv value
    }

    infix fun String.kvNotNull(value: String?) {
        if (value != null) this kv value
    }

    infix fun String.kvNotNull(value: Boolean?) {
        if (value != null) this kv value
    }

    infix fun String.kvNotNull(value: Number?) {
        if (value != null) this kv value
    }

    infix fun String.kvNotNull(value: Char?) {
        if (value != null) this kv value
    }
}
