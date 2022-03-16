package com.zhangke.notionlib.data

import com.google.gson.*
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.zhangke.framework.utils.json
import com.zhangke.framework.utils.sharedGson
import com.zhangke.framework.utils.toJsonTree
import com.zhangke.notionlib.data.block.ChildrenBlock
import java.lang.reflect.Type

@JsonAdapter(NotionBlockTypeAdapter::class)
data class NotionBlock(

    @SerializedName("object")
    val objectType: String,

    val id: String,

    val type: String,

    @SerializedName("created_time")
    val createdTime: String?,

    @SerializedName("created_by")
    val createdBy: User?,

    @SerializedName("last_edited_time")
    val lastEditedTime: String?,

    @SerializedName("last_edited_by")
    val lastEditedBy: User?,

    val archived: Boolean = false,

    @SerializedName("has_children")
    val hasChildren: Boolean = true,

    val childrenBlock: ChildrenBlock? = null
)

class NotionBlockTypeAdapter : JsonSerializer<NotionBlock>, JsonDeserializer<NotionBlock> {

    override fun serialize(
        src: NotionBlock?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        if (src == null) {
            return JsonNull.INSTANCE
        }
        return json {
            "object" kv src.objectType
            "id" kv src.id
            "created_time" kvNotNull src.createdTime
            "created_by" kvNotNull src.createdBy.toJsonTree()
            "last_edited_time" kvNotNull src.lastEditedTime
            "last_edited_by" kvNotNull src.lastEditedBy.toJsonTree()
            "archived" kv src.archived
            "has_children" kv src.hasChildren
            "type" kv src.type
            src.type kvNotNull src.childrenBlock?.toJsonTree()
        }
    }

    override fun deserialize(
        jsonElement: JsonElement,
        p1: Type?,
        p2: JsonDeserializationContext?
    ): NotionBlock {
        val json = jsonElement.asJsonObject
        val type = json.get("type").asString
        var childrenBlock: ChildrenBlock? = null
        val childrenBlockJson = json.get(type)
        if (childrenBlockJson != null) {
            childrenBlock = sharedGson.fromJson(childrenBlockJson, ChildrenBlock::class.java)
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
            childrenBlock = childrenBlock
        )
    }
}