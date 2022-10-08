package com.zhangke.notionlight.draft.db

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
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
    val blockType: String
)

@Dao
interface DraftBoxDao {

    @Query("SELECT * FROM $DRAFT_TABLE_NAME")
    fun queryAll(): Flow<List<DraftEntry>>

    @Query("SELECT * FROM $DRAFT_TABLE_NAME WHERE draftId=:id")
    suspend fun queryById(id: Long): DraftEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: DraftEntry)

    @Delete
    suspend fun delete(entry: DraftEntry)
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