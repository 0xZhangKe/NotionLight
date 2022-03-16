package com.zhangke.framework.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.internal.`$Gson$Types`
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.lang.reflect.ParameterizedType

object GsonInstance {

    @JvmField
    val globalGson: Gson = initGson()

    private fun initGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapterFactory(EnumTypeAdapterFactory())
            .create()
    }
}

private class EnumTypeAdapterFactory : TypeAdapterFactory {
    override fun <T : Any?> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        val rawType = type.rawType
        if (Enum::class.java.isAssignableFrom(rawType) && JsonEnum::class.java.isAssignableFrom(rawType)) {
            @Suppress("UNCHECKED_CAST")
            return EnumWithJsonValueTypeAdapter(gson, rawType) as TypeAdapter<T>
        }
        return null
    }
}

private class EnumWithJsonValueTypeAdapter(val gson: Gson, val rawType: Class<*>) : TypeAdapter<Any>() {
    override fun write(out: JsonWriter, value: Any?) {
        if (value == null) {
            out.nullValue()
            return
        }
        val jsonValueObject = (value as JsonEnum<*>).value
        if (jsonValueObject == null) {
            out.nullValue()
            return
        }
        val adapter = gson.getAdapter(jsonValueObject.javaClass)
        adapter.write(out, jsonValueObject)
    }

    override fun read(`in`: JsonReader): Any? {
        val genericInterface = rawType.genericInterfaces.first {
            `$Gson$Types`.getRawType(it) == JsonEnum::class.java
        }
        val valueType = (genericInterface as ParameterizedType).actualTypeArguments[0]
        val adapter = gson.getAdapter(TypeToken.get(valueType))
        val jsonValueObject = adapter.read(`in`)
        return rawType.enumConstants?.firstOrNull { (it as JsonEnum<*>).value == jsonValueObject }
    }
}

interface JsonEnum<T> {
    val value: T
}
