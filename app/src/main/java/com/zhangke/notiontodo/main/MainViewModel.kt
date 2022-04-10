package com.zhangke.notiontodo.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.notionlib.data.NotionBlock
import com.zhangke.notiontodo.config.NotionPageConfig
import com.zhangke.notiontodo.config.NotionPageConfigRepo
import com.zhangke.notiontodo.config.NotionPageType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    val pageConfigList = MutableStateFlow<List<NotionPageConfig>?>(emptyList())

    init {
        viewModelScope.launch {
            val list = listOf<NotionPageConfig>(
                NotionPageConfig("1", "TODO", NotionPageType.TODO),
                NotionPageConfig("2", "TEXT", NotionPageType.TEXT),
//                NotionPageConfig("3", "CALLOUT", NotionPageType.CALLOUT),
//                NotionPageConfig("1", "TODO", NotionPageType.TODO),
//                NotionPageConfig("2", "TEXT", NotionPageType.TEXT),
//                NotionPageConfig("3", "CALLOUT", NotionPageType.CALLOUT),
//                NotionPageConfig("1", "TODO", NotionPageType.TODO),
//                NotionPageConfig("2", "TEXT", NotionPageType.TEXT),
//                NotionPageConfig("3", "CALLOUT", NotionPageType.CALLOUT),
//                NotionPageConfig("1", "TODO", NotionPageType.TODO),
//                NotionPageConfig("2", "TEXT", NotionPageType.TEXT),
//                NotionPageConfig("3", "CALLOUT", NotionPageType.CALLOUT),
            )
            pageConfigList.emit(list)
//            withContext(Dispatchers.IO) {
//                NotionPageConfigRepo.getPageConfigList()
//            }.collect {
//                pageConfigList.emit(it)
//            }
        }
        startSyncBlocks()
    }

    suspend fun getPageBlockList(pageId: String): Flow<List<NotionBlock>?> {
        return NotionPageConfigRepo.getBlockWithPageId(pageId)
    }

    private fun startSyncBlocks(){

    }
}