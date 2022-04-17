package com.zhangke.notiontodo.config

import androidx.room.TypeConverter
import com.zhangke.framework.utils.sharedGson
import com.zhangke.notionlib.data.NotionBlock

class NotionPageRoomConverters {

    @TypeConverter
    fun toNotionPageType(value: String): NotionPageType = enumValueOf(value)

    @TypeConverter
    fun fromNotionPageType(value: NotionPageType): String = value.name

    @TypeConverter
    fun toNotionBlock(value: String): NotionBlock {
        return sharedGson.fromJson(value, NotionBlock::class.java)
    }

    @TypeConverter
    fun fromNotionBlock(value: NotionBlock): String {
        return sharedGson.toJson(value)
    }
}