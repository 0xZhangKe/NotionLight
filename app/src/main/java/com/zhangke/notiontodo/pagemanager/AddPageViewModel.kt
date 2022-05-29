package com.zhangke.notiontodo.pagemanager

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.notionlib.NotionRepo
import com.zhangke.notionlib.auth.NotionAuthorization
import com.zhangke.notionlib.data.NotionPage
import com.zhangke.notionlib.ext.getSimpleText
import com.zhangke.notiontodo.config.NotionPageConfig
import com.zhangke.notiontodo.config.NotionPageConfigRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddPageViewModel : ViewModel() {

    val notionPageList = MutableStateFlow<List<PageToAdded>?>(emptyList())
    val loading = MutableLiveData(true)

    init {
        loadPage()
        viewModelScope.launch {
            NotionAuthorization.loginStateFlow.collect {
                if (it) {
                    loadPage()
                }
            }
        }
    }

    private fun loadPage() {
        loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val allPage = NotionRepo.queryAllPages()
            val addedPage = NotionPageConfigRepo.getPageConfigList()
                .firstOrNull()
                ?.map { it.id }
                ?.toSet()
            notionPageList.emit(allPage.map {
                PageToAdded(it, it.getTitle(), addedPage?.contains(it.id) ?: false)
            })
            withContext(Dispatchers.Main) {
                loading.value = false
            }
        }
    }

    suspend fun savePage() {
        withContext(Dispatchers.IO) {
            val oldPageList = NotionPageConfigRepo.getPageConfigList().firstOrNull() ?: emptyList()
            val newPageList = notionPageList.value
                ?.filter { it.added }
                ?.map { it.page.convertToPageConfig() }
                ?: emptyList()

            val needAddedList = newPageList.filter { item ->
                oldPageList.firstOrNull { it.id == item.id } == null
            }

            val needDeleteList = oldPageList.filter { item ->
                newPageList.firstOrNull { it.id == item.id } == null
            }

            if (needAddedList.isNotEmpty()) {
                NotionPageConfigRepo.insertPageConfig(needAddedList)
            }

            if (needDeleteList.isNotEmpty()) {
                NotionPageConfigRepo.deletePageConfig(needDeleteList)
            }
        }
    }

    private fun NotionPage.convertToPageConfig(): NotionPageConfig {
        return NotionPageConfig(
            id = id,
            title = getTitle(),
            url = url,
            lastEditTime = System.currentTimeMillis()
        )
    }

    private fun NotionPage.getTitle(): String {
        return properties?.title?.title?.getSimpleText().orEmpty()
    }

    class PageToAdded(
        val page: NotionPage,
        val title: String,
        var added: Boolean = false
    )
}