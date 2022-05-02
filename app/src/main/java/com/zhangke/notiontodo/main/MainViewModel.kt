package com.zhangke.notiontodo.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.utils.DataWithLoading
import com.zhangke.framework.utils.Optional
import com.zhangke.notionlib.NotionApi
import com.zhangke.notionlib.NotionRepo
import com.zhangke.notionlib.auth.NotionAuthorization
import com.zhangke.notionlib.data.NotionBlock
import com.zhangke.notionlib.data.NotionPage
import com.zhangke.notiontodo.config.NotionPageConfig
import com.zhangke.notiontodo.config.NotionPageConfigRepo
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    val pageConfigList = MutableLiveData<List<NotionPageConfig>>()

    private val blockFlowToPageMap =
        mutableMapOf<String, MutableStateFlow<DataWithLoading<List<NotionBlock>>>>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            NotionPageConfigRepo.getPageConfigList()
                .collect {
                    withContext(Dispatchers.Main) {
                        pageConfigList.value = it
                    }
                }
        }
    }

    fun getUserIcon(): Single<Optional<String>> {
        return NotionAuthorization.readTokenSubject
            .map {
                Optional.of(NotionAuthorization.getOauthToken()?.workspaceIcon)
            }
    }

    fun getPageBlockList(pageId: String): MutableStateFlow<DataWithLoading<List<NotionBlock>>> {
        val blockListFlow = blockFlowToPageMap.getOrPut(pageId) {
            MutableStateFlow(DataWithLoading.loading())
        }
        viewModelScope.launch(Dispatchers.IO) {
            blockListFlow.emit(DataWithLoading.loading())
            NotionPageConfigRepo.getBlockWithPageId(pageId)
                .catch { blockListFlow.emit(DataWithLoading.failed(exception = it)) }
                .collect {
                    Log.d("B_TEST", "on db update:$it")
                    blockListFlow.emit(DataWithLoading.success(it))
                }
        }
        startSyncBlocks(pageId)
        return blockListFlow
    }

    fun refresh(pageId: String) {
        Log.d("B_TEST", "start refresh")
        val blockFlow = blockFlowToPageMap[pageId]
        viewModelScope.launch(Dispatchers.IO) {
            blockFlow?.emit(DataWithLoading.loading(blockFlow.value.data))
            val blockList = NotionRepo.queryAllBlocks(pageId)
            Log.d("B_TEST", "queryAllBlocks $blockList")
            NotionPageConfigRepo.insetBlocks(pageId, blockList)
            Log.d("B_TEST", "insert")
        }
    }

    private fun startSyncBlocks(pageId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val blockList = NotionRepo.queryAllBlocks(pageId)
            NotionPageConfigRepo.insetBlocks(pageId, blockList)
        }
    }
}