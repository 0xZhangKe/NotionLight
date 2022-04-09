package com.zhangke.notiontodo.config

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NotionPageConfigRepo {

    /**
     * insert or update
     */
    suspend fun insertPage(config: NotionPageConfig) {

    }

    suspend fun getPageList(): Flow<List<NotionPageConfig>> {
        return flow { emit(emptyList()) }
    }
}