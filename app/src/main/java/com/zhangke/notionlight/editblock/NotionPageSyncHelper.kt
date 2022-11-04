package com.zhangke.notionlight.editblock

import com.zhangke.architect.coroutines.ApplicationScope
import com.zhangke.notionlib.NotionRepo
import com.zhangke.notionlight.config.NotionPageConfigRepo
import kotlinx.coroutines.launch

object NotionPageSyncHelper {

    suspend fun sync(pageId: String) {
        ApplicationScope.launch {
            val blockList = NotionRepo.queryAllBlocks(pageId)
            NotionPageConfigRepo.insetOrUpdateBlocks(pageId, blockList)
        }
    }
}