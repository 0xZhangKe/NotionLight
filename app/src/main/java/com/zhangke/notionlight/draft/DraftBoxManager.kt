package com.zhangke.notionlight.draft

import com.zhangke.notionlight.draft.db.DraftBoxDatabase
import com.zhangke.notionlight.draft.db.DraftEntry
import kotlinx.coroutines.flow.Flow

object DraftBoxManager {

    private val draftDao = DraftBoxDatabase.instance.draftBoxDao()

    fun generateId(): Long {
        return System.currentTimeMillis()
    }

    suspend fun getDraftById(draftId: Long): DraftEntry? {
        return draftDao.queryById(draftId)
    }

    suspend fun saveDraft(id: Long, pageId: String, blockType: String, content: String, date: String) {
        draftDao.insert(DraftEntry(id, pageId = pageId, blockType = blockType, content = content, date = date))
    }

    suspend fun deleteDraft(draftId: Long) {
        draftDao.deleteByDraftId(draftId)
    }

    suspend fun clearDraftBox(){
        draftDao.nukeTable()
    }

    fun collectAllDraft(): Flow<List<DraftEntry>> {
        return draftDao.queryAll()
    }
}