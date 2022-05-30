package com.zhangke.notionlight.config

import androidx.room.TypeConverter
import com.zhangke.framework.utils.sharedGson
import com.zhangke.notionlib.data.NotionBlock

class NotionPageRoomConverters {

    @TypeConverter
    fun toNotionBlock(value: String): NotionBlock {
        return sharedGson.fromJson(value, NotionBlock::class.java)
    }

    @TypeConverter
    fun fromNotionBlock(value: NotionBlock): String {
        return sharedGson.toJson(value)
    }
}