package com.zhangke.framework.utils

import androidx.datastore.preferences.protobuf.Internal
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.annotations.SerializedName
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
        if (Enum::class.java.isAssignableFrom(rawType) && EnumWithJsonValue::class.java.isAssignableFrom(rawType)) {
            @Suppress("UNCHECKED_CAST")
            return EnumWithJsonValueTypeAdapter(gson, rawType) as TypeAdapter<T>
        }
        if (Enum::class.java.isAssignableFrom(rawType) && Internal.EnumLite::class.java.isAssignableFrom(rawType)) {
            @Suppress("UNCHECKED_CAST")
            return EnumLiteTypeAdapter(gson.getAdapter(Int::class.java), rawType) as TypeAdapter<T>
        }
        return null
    }
}

private class EnumLiteTypeAdapter(val intAdapter: TypeAdapter<Int>, rawType: Class<*>) : TypeAdapter<Internal.EnumLite>() {
    private val enumValues: Map<Int, Internal.EnumLite>
    private val unrecognizedValue: Internal.EnumLite?

    init {
        val map = hashMapOf<Int, Internal.EnumLite>()
        var unrecognizedValue: Internal.EnumLite? = null
        rawType.enumConstants.forEach {
            val enumValue = it as Internal.EnumLite
            try {
                map[enumValue.number] = enumValue
            } catch (e: IllegalArgumentException) {
                unrecognizedValue = enumValue
            }
        }
        this.unrecognizedValue = unrecognizedValue
        this.enumValues = map
    }

    override fun write(out: JsonWriter, value: Internal.EnumLite?) {
        if (value == null) {
            out.nullValue()
            return
        }
        if (value === unrecognizedValue) {
            intAdapter.write(out, -1)
            return
        }
        intAdapter.write(out, value.number)
    }

    override fun read(`in`: JsonReader?): Internal.EnumLite? {
        val number = intAdapter.read(`in`)
        return enumValues[number]
    }
}

private class EnumWithJsonValueTypeAdapter(val gson: Gson, val rawType: Class<*>) : TypeAdapter<Any>() {
    override fun write(out: JsonWriter, value: Any?) {
        if (value == null) {
            out.nullValue()
            return
        }
        val jsonValueObject = (value as EnumWithJsonValue<*>).value
        if (jsonValueObject == null) {
            out.nullValue()
            return
        }
        val adapter = gson.getAdapter(jsonValueObject.javaClass)
        adapter.write(out, jsonValueObject)
    }

    override fun read(`in`: JsonReader): Any? {
        val genericInterface = rawType.genericInterfaces.first {
            `$Gson$Types`.getRawType(it) == EnumWithJsonValue::class.java
        }
        val valueType = (genericInterface as ParameterizedType).actualTypeArguments[0]
        val adapter = gson.getAdapter(TypeToken.get(valueType))
        val jsonValueObject = adapter.read(`in`)
        return rawType.enumConstants?.firstOrNull { (it as EnumWithJsonValue<*>).value == jsonValueObject }
    }
}

/**
 * 将Enum的解析和序列化转换为对[value]的解析和序列化，适合服务端数据为几个固定的值的情况。
 * 这个方案比[SerializedName]方便的地方在于，你可以方便地直接使用[value]的值，而[SerializedName]注解在枚举对象上，不方便使用，而且[T]可以是任意类型。
 */
interface EnumWithJsonValue<T> {
    val value: T
}
