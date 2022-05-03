package com.zhangke.notiontodo.config

import com.zhangke.notionlib.data.NotionBlock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object NotionPageConfigRepo {

    /**
     * insert or update
     */
    suspend fun insertPageConfig(config: List<NotionPageConfig>) {
        NotionPageDataBase.instance
            .pageConfigDao()
            .insetConfig(config)
    }

    suspend fun getPageConfigList(): Flow<List<NotionPageConfig>> {
        return NotionPageDataBase.instance
            .pageConfigDao()
            .queryAllConfig()
    }

    suspend fun deletePageConfig(configs: List<NotionPageConfig>) {
        return NotionPageDataBase.instance
            .pageConfigDao()
            .deletePage(configs)
    }

    suspend fun insetBlocks(pageId: String, block: List<NotionBlock>) {
        NotionPageDataBase.instance
            .blockInPageDao()
            .insetBlocks(block.map { NotionBlockInPage(it.id, pageId, it) })
    }

    suspend fun getBlockWithPageId(id: String): Flow<List<NotionBlock>> {
        return NotionPageDataBase.instance
            .blockInPageDao()
            .queryBlockWithPageId(id)
            .map {
                it.map { item -> item.notionBlock }
                    .sortedByDescending { item -> item.lastEditedDate?.time ?: 0 }
            }
    }

    suspend fun deletePageAllBlock(pageId: String) {
        NotionPageDataBase.instance
            .blockInPageDao()
            .deletePageAllBlock(pageId)
    }
}