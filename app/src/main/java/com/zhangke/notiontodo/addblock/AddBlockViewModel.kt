package com.zhangke.notiontodo.addblock

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.notiontodo.config.NotionPageConfig
import com.zhangke.notiontodo.config.NotionPageConfigRepo
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AddBlockViewModel : ViewModel() {

    val currentPage = MutableLiveData<NotionPageConfig?>()
    val currentBlockType = MutableLiveData<String?>()

    private var targetPageName: String? = null
        set(value) {
            field = value
            findTargetPage()
        }

    private var pageList: List<NotionPageConfig>? = null
        set(value) {
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
        targetPageName = intent.getStringExtra(AddBlockActivity.INTENT_ARG_PAGE)
    }

    private fun findTargetPage() {
        val pageList = pageList ?: return
        currentPage.value =
            pageList.firstOrNull {
                if (targetPageName.isNullOrEmpty()) {
                    true
                } else {
                    it.title == targetPageName
                }
            }
        currentBlockType.value = currentPage.value?.type?.value
    }
}