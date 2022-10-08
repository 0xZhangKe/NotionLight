package com.zhangke.notionlight.editblock

import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.utils.toast
import com.zhangke.notionlib.NotionRepo
import com.zhangke.notionlib.data.NotionBlock
import com.zhangke.notionlib.data.block.BlockType
import com.zhangke.notionlib.ext.getLightText
import com.zhangke.notionlight.R
import com.zhangke.notionlight.addblock.NotionPageSyncHelper
import com.zhangke.notionlight.config.NotionBlockInPage
import com.zhangke.notionlight.config.NotionPageConfig
import com.zhangke.notionlight.config.NotionPageConfigRepo
import com.zhangke.notionlight.draft.DraftBoxManager
import com.zhangke.notionlight.draft.db.DraftEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.properties.Delegates

class EditBlockViewModel : ViewModel() {

    val viewStates = NotionBlockViewState()

    private val pageListFlow = MutableSharedFlow<List<NotionPageConfig>>(1)
    private val draftEntryFlow = MutableSharedFlow<DraftEntry>(1)
    private val savedNotionBlock = MutableSharedFlow<NotionBlockInPage>(1)

    private var currentDraftId by Delegates.notNull<Long>()
    private var blockId: String? = null

    var block = MutableLiveData<NotionBlock?>()

    var onAddSuccess: (() -> Unit)? = null

    init {
        initSharedData()
    }

    private fun initSharedData() {
        viewModelScope.launch {
            val list = NotionPageConfigRepo.getPageConfigList()
                .catch { }
                .first()
            viewStates.pageList.emit(list.map { NotionPage(it.title, it.id) })
            pageListFlow.emit(list)
        }
    }

    fun parseIntent(intent: Intent) {
        var draftId: Long? = null
        val localDraftId = intent.getLongExtra(EditBlockActivity.INTENT_ARG_DRAFT_ID, -1L)
        val pageId: String? = intent.getStringExtra(EditBlockActivity.INTENT_ARG_PAGE_ID)
        val blockId: String? = intent.getStringExtra(EditBlockActivity.INTENT_ARG_BLOCK_ID)

        if (localDraftId != -1L) {
            draftId = localDraftId
        }
        currentDraftId = draftId ?: DraftBoxManager.generateId()
        val isEditModel = draftId == null && blockId != null
        this.blockId = blockId
        viewStates.canEditPage.tryEmit(isEditModel)
        initSharedDataByParams(draftId, blockId)
        initState(pageId, draftId, blockId)
    }

    private fun initSharedDataByParams(draftId: Long?, blockId: String?) {
        if (draftId != null) {
            viewModelScope.launch {
                val draft = DraftBoxManager.getDraftById(draftId)
                if (draft == null) {
                    withContext(Dispatchers.Main) {
                        toast(R.string.draft_not_found, Toast.LENGTH_LONG)
                    }
                    return@launch
                }
                draftEntryFlow.emit(draft)
            }
        }
        if (blockId != null) {
            viewModelScope.launch {
                val notionBlock = NotionPageConfigRepo.queryBlock(blockId!!)
                if (notionBlock != null) {
                    savedNotionBlock.emit(notionBlock)
                }
            }
        }
    }

    private fun initState(pageId: String?, draftId: Long?, blockId: String?) {
        viewModelScope.launch {
            val pageConfig = getPageConfig(pageId, draftId, blockId)
            if (pageConfig == null) {
                toast(R.string.get_notion_page_error, Toast.LENGTH_LONG)
                return@launch
            }
            viewStates.currentPage.emit(NotionPage(pageConfig.title, pageConfig.id))
            initBlockType(pageConfig.id, draftId, blockId)
            initBlockContent(draftId, blockId)
        }
    }

    private suspend fun getPageConfig(
        pageId: String?,
        draftId: Long?,
        blockId: String?
    ): NotionPageConfig? {
        val config = if (pageId != null) {
            getPageConfigByPageId(pageId)
        } else if (draftId != null) {
            getPageConfigByDraft()
        } else if (blockId != null) {
            getPageConfigByBlockId()
        } else {
            null
        }
        return config
    }

    private suspend fun getPageConfigByPageId(pageId: String): NotionPageConfig? {
        val pageList = pageListFlow.first()
        return pageList.firstOrNull { it.id == pageId } ?: pageList.firstOrNull()
    }

    private suspend fun getPageConfigByDraft(): NotionPageConfig? {
        val draft = draftEntryFlow.firstOrNull() ?: return null
        return getPageConfigByPageId(draft.pageId)
    }

    private suspend fun getPageConfigByBlockId(): NotionPageConfig? {
        val blockConfig = savedNotionBlock.firstOrNull() ?: return null
        return getPageConfigByPageId(blockConfig.pageId)
    }

    private suspend fun initBlockType(pageId: String, draftId: Long?, blockId: String?) {
        val blockType = when {
            draftId != null -> getBlockTypeByDraft()
            blockId != null -> getBlockTypeByBlockId()
            else -> getBlockTypeByPageId(pageId)
        } ?: BlockType.PARAGRAPH
        viewStates.blockType.emit(blockType)
    }

    private suspend fun getBlockTypeByDraft(): String? {
        val draft = draftEntryFlow.firstOrNull()
        return draft?.blockType
    }

    private suspend fun getBlockTypeByBlockId(): String? {
        val notionBlock = savedNotionBlock.firstOrNull()
        return notionBlock?.notionBlock?.type
    }

    private suspend fun getBlockTypeByPageId(pageId: String): String? {
        val latestBlockList = try {
            NotionPageConfigRepo.queryLatestBlockWithPageIdByTime(pageId)
        } catch (e: Exception) {
            return null
        }
        val typeToCount = mutableMapOf<String, Int>()
        latestBlockList.forEach {
            val count = typeToCount[it.notionBlock.type] ?: 0
            typeToCount[it.notionBlock.type] = count + 1
        }
        var maxCount = -1
        var type = BlockType.PARAGRAPH
        typeToCount.entries.forEach {
            if (it.value > maxCount) {
                maxCount = it.value
                type = it.key
            }
        }
        return type
    }

    private suspend fun initBlockContent(draftId: Long?, blockId: String?) {
        val blockContent = if (draftId != null) {
            getBlockContentByDraft()
        } else if (blockId != null) {
            getBlockContentByBlockId()
        } else {
            return
        }
        viewStates.content.emit(blockContent)
    }

    private suspend fun getBlockContentByDraft(): String? {
        return draftEntryFlow.firstOrNull()?.content
    }

    private suspend fun getBlockContentByBlockId(): String? {
        return savedNotionBlock.firstOrNull()?.notionBlock?.getLightText()
    }

    fun onInputContentChanged(newContent: String) {
        viewStates.content.tryEmit(newContent)
        saveDraft()
    }

    private fun getCurrentTime(): String {
        val format = SimpleDateFormat("HH:mm:ss")
        return format.format(Date())
    }

    fun onPageSelected(newPage: NotionPage) {
        viewStates.currentPage.tryEmit(newPage)
        saveDraft()
    }

    fun onBlockTypeChanged(newType: String) {
        viewStates.blockType.tryEmit(newType)
        saveDraft()
    }

    private fun saveDraft() {
        viewModelScope.launch {
            viewStates.savingState.emit("Saving...")
            val content = viewStates.content.first().orEmpty()
            val currentPageId = viewStates.currentPage.first().pageId
            val currentBlockType = viewStates.blockType.first()
            DraftBoxManager.saveDraft(currentDraftId, currentPageId, currentBlockType, content)
            viewStates.savingState.emit("Draft saved on ${getCurrentTime()}")
        }
    }

    fun onConfirm() {
        viewModelScope.launch {
            if (blockId != null) {
                requestEditBlock(blockId!!)
            } else {
                requestNewBlock()
            }
        }
    }

    private suspend fun requestEditBlock(blockId: String) {

    }

    private suspend fun requestNewBlock() {

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

    class NotionPage(
        val pageName: String,
        val pageId: String
    )
}