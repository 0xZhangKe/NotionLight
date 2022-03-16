package com.zhangke.notionlib.data.block

import com.google.gson.*
import com.google.gson.annotations.JsonAdapter
import com.zhangke.framework.utils.json
import com.zhangke.framework.utils.sharedGson
import com.zhangke.framework.utils.toJsonTree
import java.lang.reflect.Type

@JsonAdapter(ChildrenBlockTypeAdapter::class)
data class ChildrenBlock(
    val type: String,
    val content: TypedBlock?
)

class ChildrenBlockTypeAdapter : JsonSerializer<ChildrenBlock>, JsonDeserializer<ChildrenBlock> {

    private val typedClassMap = mutableMapOf<String, Class<out TypedBlock>>()

    init {
        typedClassMap["to_do"] = TodoBlock::class.java
        typedClassMap["paragraph"] = ParagraphBlock::class.java
        typedClassMap["heading_1"] = HeadingBlock::class.java
        typedClassMap["heading_2"] = HeadingBlock::class.java
        typedClassMap["heading_3"] = HeadingBlock::class.java
        typedClassMap["callout"] = CalloutBlock::class.java
        typedClassMap["quote"] = QuoteBlock::class.java
    }

    override fun serialize(
        src: ChildrenBlock?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        if (src == null) {
            return JsonNull.INSTANCE
        }
        return json {
            "type" kv src.type
            src.type kvNotNull src.content.toJsonTree()
        }
    }

    override fun deserialize(
        jsonElement: JsonElement,
        p1: Type?,
        p2: JsonDeserializationContext?
    ): ChildrenBlock {
        val json = jsonElement.asJsonObject
        val type = json.get("type").asString
        var content: TypedBlock? = null
        val contentJson = json.get(type)
        if (contentJson != null) {
            val clazz = typedClassMap[type]
            content = if (clazz != null) {
                sharedGson.fromJson(contentJson, clazz)
            } else {
                UndefinedBlock(contentJson.asJsonObject)
            }
        }
        return ChildrenBlock(
            type = type,
            content = content
        )
    }
}