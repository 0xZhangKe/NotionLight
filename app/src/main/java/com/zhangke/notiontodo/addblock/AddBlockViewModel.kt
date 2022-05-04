package com.zhangke.notiontodo.addblock

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.utils.toast
import com.zhangke.notionlib.NotionRepo
import com.zhangke.notionlib.data.block.BlockType
import com.zhangke.notiontodo.R
import com.zhangke.notiontodo.config.NotionPageConfig
import com.zhangke.notiontodo.config.NotionPageConfigRepo
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AddBlockViewModel : ViewModel() {

    val currentPage = MutableLiveData<NotionPageConfig?>()
    val currentBlockType = MutableLiveData<String?>()

    val currentInputText = MutableLiveData<String>()

    val blockTypeList = listOf(
        BlockType.CALLOUT,
        BlockType.PARAGRAPH,
        BlockType.TODO
    )

    var onAddSuccess: (() -> Unit)? = null

    private var targetPageId: String? = null
        set(value) {
            field = value
            findTargetPage()
        }

    var pageList: List<NotionPageConfig>? = null
        private set(value) {
            field = value
            findTargetPage()
        }

    init {
        viewModelScope.launch {
            NotionPageConfigRepo.getPageConfigList()
                .catch { }
                .collect { pageList = it }
        }
    }

    fun parseIntent(intent: Intent) {
        targetPageId = intent.getStringExtra(AddBlockActivity.INTENT_ARG_PAGE)
    }

    fun saveContent() {
        val pageId = currentPage.value?.id ?: return
        val blockType = currentBlockType.value ?: return
        val inputtedText = currentInputText.value
        if (inputtedText.isNullOrEmpty()) {
            toast(R.string.input_empty)
            return
        }
        viewModelScope.launch {
            val response = NotionRepo.appendBlock(inputtedText, pageId, blockType)
            response.onSuccess {
                onAddSuccess?.invoke()
            }
            response.onError {
                toast(it.message)
            }
            NotionPageSyncHelper.sync(pageId)
        }
    }

    private fun findTargetPage() {
        val pageList = pageList ?: return
        currentPage.value =
            pageList.firstOrNull {
                if (targetPageId.isNullOrEmpty()) {
                    true
                } else {
                    it.id == targetPageId
                }
            }
        currentBlockType.value = currentPage.value?.type
    }
}