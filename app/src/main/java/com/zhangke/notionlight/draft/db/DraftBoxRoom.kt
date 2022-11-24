package com.zhangke.notionlight.draft.db

import androidx.room.*
import com.zhangke.framework.utils.appContext
import kotlinx.coroutines.flow.Flow

private const val DB_NAME = "draft_box.db"
private const val DB_VERSION = 1

private const val DRAFT_TABLE_NAME = "draft"

@Entity(tableName = DRAFT_TABLE_NAME)
data class DraftEntry(
    @PrimaryKey val draftId: Long,
    val content: String,
    val pageId: String,
    val blockType: String,
    val date: String
)

@Dao
interface DraftBoxDao {

    @Query("SELECT * FROM $DRAFT_TABLE_NAME")
    fun queryAll(): Flow<List<DraftEntry>>

    @Query("SELECT * FROM $DRAFT_TABLE_NAME WHERE draftId=:id")
    suspend fun queryById(id: Long): DraftEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: DraftEntry)

    @Query("DELETE FROM $DRAFT_TABLE_NAME WHERE draftId=:draftId")
    suspend fun deleteByDraftId(draftId: Long)

    @Query("DELETE FROM $DRAFT_TABLE_NAME")
    suspend fun nukeTable()
}

@Database(entities = [DraftEntry::class], version = DB_VERSION)
abstract class DraftBoxDatabase : RoomDatabase() {

    abstract fun draftBoxDao(): DraftBoxDao

    companion object {

        val instance: DraftBoxDatabase by lazy { createDatabase() }

        private fun createDatabase(): DraftBoxDatabase {
            return Room.databaseBuilder(appContext, DraftBoxDatabase::class.java, DB_NAME)
                .build()
        }
    }
}