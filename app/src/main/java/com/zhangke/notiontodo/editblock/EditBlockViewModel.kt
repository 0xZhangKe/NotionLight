package com.zhangke.notiontodo.editblock

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.utils.toast
import com.zhangke.notionlib.NotionRepo
import com.zhangke.notionlib.data.NotionBlock
import com.zhangke.notiontodo.addblock.NotionPageSyncHelper
import com.zhangke.notiontodo.config.NotionPageConfigRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditBlockViewModel : ViewModel() {

    private lateinit var pageId: String
    private lateinit var blockId: String
    var block = MutableLiveData<NotionBlock?>()

    var onAddSuccess: (() -> Unit)? = null

    fun parseIntent(intent: Intent) {
        pageId = intent.getStringExtra(EditBlockActivity.INTENT_ARG_PAGE_ID)!!
        blockId = intent.getStringExtra(EditBlockActivity.INTENT_ARG_BLOCK_ID)!!
        updateBlockData()
    }

    private fun updateBlockData() {
        viewModelScope.launch {
            block.value = withContext(Dispatchers.IO) {
                NotionPageConfigRepo.queryBlock(blockId)?.notionBlock
            }
        }
    }

    fun update(content: String) {
        val block = block.value ?: return
        viewModelScope.launch {
            val response = NotionRepo.updateBlock(block, content)
            response.onError {
                toast(it.message)
            }

            response.onSuccess {
                onAddSuccess?.invoke()
                NotionPageSyncHelper.sync(pageId)
            }
        }
    }
}