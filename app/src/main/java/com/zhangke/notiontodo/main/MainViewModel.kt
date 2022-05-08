package com.zhangke.notiontodo.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.utils.DataWithLoading
import com.zhangke.framework.utils.Optional
import com.zhangke.notionlib.NotionRepo
import com.zhangke.notionlib.auth.NotionAuthorization
import com.zhangke.notionlib.data.NotionBlock
import com.zhangke.notiontodo.config.NotionPageConfig
import com.zhangke.notiontodo.config.NotionPageConfigRepo
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
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
            MutableStateFlow(DataWithLoading.idle())
        }
        viewModelScope.launch(Dispatchers.IO) {
            NotionPageConfigRepo.getBlockWithPageId(pageId)
                .catch { blockListFlow.emit(DataWithLoading.failed(exception = it)) }
                .collect {
                    val lastList = blockListFlow.value.data
                    // 由于下面的startSyncBlocks方法会先清空数据库再写入，所以这里在同步后会触发两次（包含一次空列表），
                    // 因此直接对比list会失效，这里先这么苟着，问题不大。
                    if (lastList != it && it.isNotEmpty()) {
                        blockListFlow.emit(DataWithLoading.success(it))
                    }
                }
        }
        startSyncBlocks(pageId)
        return blockListFlow
    }

    fun refresh(pageId: String) {
        val blockFlow = blockFlowToPageMap[pageId]
        viewModelScope.launch(Dispatchers.IO) {
            blockFlow?.emit(DataWithLoading.loading(blockFlow.value.data))
            val blockList = NotionRepo.queryAllBlocks(pageId)
            NotionPageConfigRepo.insetBlocks(pageId, blockList)
        }
    }

    private fun startSyncBlocks(pageId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val blockList = NotionRepo.queryAllBlocks(pageId)
            NotionPageConfigRepo.deletePageAllBlock(pageId)
            NotionPageConfigRepo.insetBlocks(pageId, blockList)
        }
    }
}