package com.zhangke.notiontodo.addblock

import com.zhangke.architect.coroutines.ApplicationScope
import com.zhangke.notionlib.NotionRepo
import com.zhangke.notiontodo.config.NotionPageConfigRepo
import kotlinx.coroutines.launch

object NotionPageSyncHelper {

    fun sync(pageId: String) {
        ApplicationScope.launch {
            val blockList = NotionRepo.queryAllBlocks(pageId)
            NotionPageConfigRepo.deletePageAllBlock(pageId)
            NotionPageConfigRepo.insetBlocks(pageId, blockList)
        }
    }
}