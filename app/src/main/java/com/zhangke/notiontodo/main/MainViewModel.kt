package com.zhangke.notiontodo.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.notionlib.NotionRepo
import com.zhangke.notionlib.data.NotionBlock
import com.zhangke.notiontodo.config.NotionPageConfig
import com.zhangke.notiontodo.config.NotionPageConfigRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val pageConfigList = MutableStateFlow<List<NotionPageConfig>?>(emptyList())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            NotionPageConfigRepo.getPageConfigList()
                .collect { pageConfigList.emit(it) }
        }
    }

    fun getPageBlockList(pageId: String): MutableStateFlow<List<NotionBlock>?> {
        val blockListFlow = MutableStateFlow<List<NotionBlock>?>(emptyList())
        viewModelScope.launch(Dispatchers.IO) {
            NotionPageConfigRepo.getBlockWithPageId(pageId)
                .collect {
                    blockListFlow.emit(it)
                }
        }
        startSyncBlocks(pageId)
        return blockListFlow
    }

    private fun startSyncBlocks(pageId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val blockList = NotionRepo.queryAllBlocks(pageId)
            NotionPageConfigRepo.insetBlocks(pageId, blockList)
        }
    }
}