package com.zhangke.notionlib.utils

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.zhangke.framework.utils.sharedGson
import com.zhangke.notionlib.data.RichText
import com.zhangke.notionlib.data.TextAnnotations
import com.zhangke.notionlib.data.block.CalloutBlock
import com.zhangke.notionlib.data.block.ParagraphBlock
import com.zhangke.notionlib.data.block.TodoBlock

object BlockBuildHelper {

    private val DEFAULT_TEXT_ANNOTATIONS = TextAnnotations(
        bold = false,
        italic = false,
        strikethrough = false,
        code = false,
        color = "default"
    )

    fun build(type: String, text: String): JsonObject {
        return JsonObject().apply {
            val children = JsonArray().apply {
                val child = JsonObject().apply {
                    addProperty("object", "block")
                    addProperty("type", type)
                    add(type, buildChildrenBlock(type, text))
                }
                add(child)
            }
            add("children", children)
        }
    }

    private fun buildChildrenBlock(type: String, text: String): JsonObject {
        val block = when (type) {
            TodoBlock.TYPE -> text.toTodoBlock()
            CalloutBlock.TYPE -> text.toCalloutBlock()
            ParagraphBlock.TYPE -> text.toParagraphBlock()
            else -> throw UnsupportedOperationException("unsupported type: $type")
        }
        return sharedGson.fromJson(sharedGson.toJson(block), JsonObject::class.java)
    }

    private fun String.toParagraphBlock(): ParagraphBlock {
        return ParagraphBlock(
            richText = listOf(buildSimpleText()),
            children = null
        )
    }

    private fun String.toCalloutBlock(): CalloutBlock {
        return CalloutBlock(
            richText = listOf(buildSimpleText()),
            children = null
        )
    }

    private fun String.toTodoBlock(): TodoBlock {
        return TodoBlock(
            richText = listOf(buildSimpleText()),
            checked = false,
            children = null
        )
    }

    private fun String.buildSimpleText(): RichText {
        return RichText(
            plainText = this,
            annotations = DEFAULT_TEXT_ANNOTATIONS,
            href = null,
            text = RichText.Text(this)
        )
    }
}