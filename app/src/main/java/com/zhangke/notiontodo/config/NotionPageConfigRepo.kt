package com.zhangke.notiontodo.config

import com.zhangke.notionlib.data.NotionBlock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object NotionPageConfigRepo {

    /**
     * insert or update
     */
    suspend fun insertPageConfig(config: NotionPageConfig) {
        NotionPageDataBase.instance
            .pageConfigDao()
            .insetConfig(config)
    }

    suspend fun getPageConfigList(): Flow<List<NotionPageConfig>> {
        return NotionPageDataBase.instance
            .pageConfigDao()
            .queryAllConfig()
    }

    suspend fun deletePageConfig(config: NotionPageConfig) {
        return NotionPageDataBase.instance
            .pageConfigDao()
            .deletePage(config)
    }

    suspend fun insetBlock(pageId: String, block: NotionBlock) {
        NotionPageDataBase.instance
            .blockInPageDao()
            .insetBlock(NotionBlockInPage(pageId, block))
    }

    suspend fun getBlockWithPageId(id: String): Flow<List<NotionBlock>> {
        return NotionPageDataBase.instance
            .blockInPageDao()
            .queryBlockWithPageId(id)
            .map {
                it.map { item -> item.notionBlock }
            }
    }

    suspend fun deletePageAllBlock(pageId: String) {
        NotionPageDataBase.instance
            .blockInPageDao()
            .deletePageAllBlock(pageId)
    }
}