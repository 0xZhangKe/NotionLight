package com.zhangke.notionlight.config

import android.content.Context
import androidx.room.*
import com.zhangke.framework.utils.appContext
import com.zhangke.notionlib.data.NotionBlock
import kotlinx.coroutines.flow.Flow

private const val DB_NAME = "notion_page.db"
private const val DB_VERSION = 1

private const val CONFIG_TABLE_NAME = "notion_page_config"
private const val BLOCK_TABLE_NAME = "notion_page_block"

@Entity(tableName = CONFIG_TABLE_NAME)
data class NotionPageConfig(
    @PrimaryKey val id: String,
    val title: String,
    val lastEditTime: Long,
    val url: String
)

@Entity(tableName = BLOCK_TABLE_NAME)
data class NotionBlockInPage(
    @PrimaryKey val id: String,
    val pageId: String,
    val editTimestamp: Long,
    val notionBlock: NotionBlock,
)

@Dao
interface NotionPageConfigDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insetConfig(config: List<NotionPageConfig>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insetConfig(config: NotionPageConfig)

    @Query("SELECT * FROM $CONFIG_TABLE_NAME")
    fun queryAllConfig(): Flow<List<NotionPageConfig>>

    @Delete
    suspend fun deletePage(config: List<NotionPageConfig>)

    @Query("DELETE FROM $CONFIG_TABLE_NAME")
    suspend fun nukeTable()
}

@Dao
interface NotionBlockInPageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insetOrUpdateBlocks(block: List<NotionBlockInPage>)

    @Query("SELECT * FROM $BLOCK_TABLE_NAME WHERE id == :blockId")
    suspend fun queryBlock(blockId: String): NotionBlockInPage?

    @Query("SELECT * FROM $BLOCK_TABLE_NAME WHERE pageId == :pageId")
    fun queryBlockWithPageId(pageId: String): Flow<List<NotionBlockInPage>>

    @Query("SELECT * FROM $BLOCK_TABLE_NAME WHERE pageId == :pageId ORDER BY editTimestamp DESC LIMIT :count OFFSET 0")
    suspend fun queryLatestBlockWithPageIdByTime(
        pageId: String,
        count: Int
    ): List<NotionBlockInPage>

    @Query("DELETE FROM $BLOCK_TABLE_NAME WHERE pageId == :pageId")
    suspend fun deletePageAllBlock(pageId: String)

    @Query("DELETE FROM $BLOCK_TABLE_NAME")
    suspend fun nukeTable()
}

@TypeConverters(NotionPageRoomConverters::class)
@Database(entities = [NotionPageConfig::class, NotionBlockInPage::class], version = DB_VERSION)
abstract class NotionPageDataBase : RoomDatabase() {

    abstract fun pageConfigDao(): NotionPageConfigDao

    abstract fun blockInPageDao(): NotionBlockInPageDao

    companion object {

        val instance: NotionPageDataBase by lazy { createInstance(appContext) }

        private fun createInstance(context: Context): NotionPageDataBase {
            return Room.databaseBuilder(context, NotionPageDataBase::class.java, DB_NAME)
                .build()
        }
    }
}