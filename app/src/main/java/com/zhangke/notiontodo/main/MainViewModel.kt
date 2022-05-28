package com.zhangke.notiontodo.main

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.utils.DataWithLoading
import com.zhangke.framework.utils.Optional
import com.zhangke.framework.utils.appContext
import com.zhangke.framework.utils.toast
import com.zhangke.notionlib.NotionRepo
import com.zhangke.notionlib.auth.NotionAuthorization
import com.zhangke.notionlib.data.NotionBlock
import com.zhangke.notionlib.ext.getLightText
import com.zhangke.notiontodo.config.NotionPageConfig
import com.zhangke.notiontodo.config.NotionPageConfigRepo
import com.zhangke.notiontodo.support.supportedEditType
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    val pageConfigList = MutableLiveData<List<NotionPageConfig>>()

    val userIcon = MutableLiveData<String?>()

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
        viewModelScope.launch {
            NotionAuthorization.loginStateFlow.collect {
                if (!it) {
                    userIcon.value = null
                }
            }
        }
        NotionAuthorization.readTokenSubject
            .subscribe({
                userIcon.value = NotionAuthorization.getOauthToken()?.workspaceIcon
            }, {})
    }

    fun getPageBlockList(pageId: String): MutableStateFlow<DataWithLoading<List<NotionBlock>>> {
        val blockListFlow = blockFlowToPageMap.getOrPut(pageId) {
            MutableStateFlow(DataWithLoading.idle())
        }
        viewModelScope.launch(Dispatchers.IO) {
            NotionPageConfigRepo.getBlockWithPageId(pageId)
                .catch { blockListFlow.emit(DataWithLoading.failed(exception = it)) }
                .collect {
                    // 由于下面的startSyncBlocks方法会先清空数据库再写入，所以这里在同步后会触发两次（包含一次空列表），
                    // 因此直接对比list会失效，这里先这么苟着，问题不大。
                    val fixedList = it.filter { item -> !item.getLightText().isNullOrEmpty() }
                    if (fixedList.isNotEmpty()) {
                        blockListFlow.emit(DataWithLoading.success(fixedList))
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

    fun copy(block: NotionBlock) {
        val clipboard = appContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val text = block.getLightText().orEmpty()
        val clip = ClipData.newPlainText(text, text)
        clipboard.setPrimaryClip(clip)
    }

    fun delete(block: NotionBlock, pageId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = NotionRepo.deleteBlock(block.id)
            response.onError {
                toast(it.message)
            }
            response.onSuccess {
                startSyncBlocks(pageId)
            }
        }
    }

    fun isBlockSupportEdit(block: NotionBlock): Boolean {
        return supportedEditType.contains(block.type)
    }

    private fun startSyncBlocks(pageId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val blockList = NotionRepo.queryAllBlocks(pageId)
            NotionPageConfigRepo.deletePageAllBlock(pageId)
            NotionPageConfigRepo.insetBlocks(pageId, blockList)
        }
    }
}