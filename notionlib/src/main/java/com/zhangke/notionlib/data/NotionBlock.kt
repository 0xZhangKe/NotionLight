package com.zhangke.notionlib.data

import com.google.gson.*
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.zhangke.framework.utils.*
import com.zhangke.notionlib.data.block.*
import com.zhangke.notionlib.utils.NotionDateConvertor
import java.lang.reflect.Type
import java.util.*

@JsonAdapter(NotionBlockTypeAdapter::class)
data class NotionBlock(

    @SerializedName("object")
    val objectType: String,

    val id: String = "",

    val type: String,

    @SerializedName("created_time")
    val createdTime: String? = null,

    @SerializedName("created_by")
    val createdBy: User? = null,

    @SerializedName("last_edited_time")
    val lastEditedTime: String? = null,

    @SerializedName("last_edited_by")
    val lastEditedBy: User? = null,

    val archived: Boolean = false,

    @SerializedName("has_children")
    val hasChildren: Boolean = true,

    val childrenBlock: TypedBlock? = null
) {
    val lastEditedDate: Date? = NotionDateConvertor.convert(lastEditedTime)
}

class NotionBlockTypeAdapter : JsonSerializer<NotionBlock>, JsonDeserializer<NotionBlock> {

    private val typedClassMap = mutableMapOf<String, Class<out TypedBlock>>()

    init {
        typedClassMap[TodoBlock.TYPE] = TodoBlock::class.java
        typedClassMap[ParagraphBlock.TYPE] = ParagraphBlock::class.java
        typedClassMap[HeadingBlock.TYPE_1] = HeadingBlock::class.java
        typedClassMap[HeadingBlock.TYPE_2] = HeadingBlock::class.java
        typedClassMap[HeadingBlock.TYPE_3] = HeadingBlock::class.java
        typedClassMap[CalloutBlock.TYPE] = CalloutBlock::class.java
        typedClassMap[QuoteBlock.TYPE] = QuoteBlock::class.java
        typedClassMap[NumberListItemBlock.TYPE] = NumberListItemBlock::class.java
        typedClassMap[BulletedListItemBlock.TYPE] = BulletedListItemBlock::class.java
    }

    override fun serialize(
        src: NotionBlock?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        if (src == null) {
            return JsonNull.INSTANCE
        }
        return JsonObject().apply {
            addProperty("object", src.objectType)
            addProperty("id", src.id)
            addStringNotNull("created_time", src.createdTime)
            addNotNull("created_by", src.createdBy.toJsonTree())
            addStringNotNull("last_edited_time", src.lastEditedTime)
            addNotNull("last_edited_by", src.lastEditedBy.toJsonTree())
            addProperty("archived", src.archived)
            addProperty("has_children", src.hasChildren)
            addProperty("type", src.type)
            addNotNull(src.type, src.childrenBlock?.toJsonTree())
        }
    }

    override fun deserialize(
        jsonElement: JsonElement,
        p1: Type?,
        p2: JsonDeserializationContext?
    ): NotionBlock {
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

        val createdBy = json.get("created_by")?.let {
            sharedGson.fromJson(it, User::class.java)
        }
        val lastEditBy = json.get("last_edited_by")?.let {
            sharedGson.fromJson(it, User::class.java)
        }
        return NotionBlock(
            objectType = json.get("object").asString,
            id = json.get("id").asString,
            type = type,
            createdTime = json.get("created_time")?.asString,
            createdBy = createdBy,
            lastEditedTime = json.get("last_edited_time")?.asString,
            lastEditedBy = lastEditBy,
            archived = json.get("archived")?.asBoolean ?: false,
            hasChildren = json.get("has_children")?.asBoolean ?: true,
            childrenBlock = content
        )
    }
}