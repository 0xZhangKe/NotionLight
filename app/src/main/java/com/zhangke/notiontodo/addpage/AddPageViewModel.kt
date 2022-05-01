package com.zhangke.notiontodo.addpage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.notionlib.NotionRepo
import com.zhangke.notionlib.data.NotionPage
import com.zhangke.notiontodo.config.NotionPageConfig
import com.zhangke.notiontodo.config.NotionPageConfigRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddPageViewModel : ViewModel() {

    val notionPageList = MutableStateFlow<List<NotionPage>?>(emptyList())
    val loading = MutableLiveData(true)

    fun loadPage() {
        loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            notionPageList.emit(NotionRepo.queryAllPages())
            withContext(Dispatchers.Main) {
                loading.value = false
            }
        }
    }

    suspend fun savePage(pageList: List<NotionPageConfig>) {
        withContext(Dispatchers.IO) {
            NotionPageConfigRepo.insertPageConfig(pageList)
        }
    }
}