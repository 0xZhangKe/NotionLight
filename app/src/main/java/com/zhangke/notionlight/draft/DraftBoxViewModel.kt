package com.zhangke.notionlight.draft

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.notionlight.draft.db.DraftEntry
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

/**
 * Created by ZhangKe on 2022/11/25.
 */
class DraftBoxViewModel : ViewModel() {

    val allDrafts = MutableSharedFlow<List<DraftEntry>>(1)

    init {
        collectAllDraft()
    }

    private fun collectAllDraft() {
        viewModelScope.launch {
            DraftBoxManager.collectAllDraft().collect {
                allDrafts.emit(it)
            }
        }
    }

    fun clearDraftBox() {
        viewModelScope.launch {
            DraftBoxManager.clearDraftBox()
        }
    }
}