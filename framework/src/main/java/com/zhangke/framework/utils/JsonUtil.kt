package com.zhangke.framework.utils

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject

val sharedGson: Gson = GsonInstance.globalGson

fun Any?.toJsonTree(): JsonElement = sharedGson.toJsonTree(this)

fun JsonObject.addStringNotNull(key: String, value: String?) {
    value?.let { addProperty(key, it) }
}

fun JsonObject.addNotNull(key: String, value: JsonElement?) {
    value?.let { add(key, it) }
}