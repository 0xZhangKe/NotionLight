package com.zhangke.notiontodo.pagemanager

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.notiontodo.config.NotionPageConfig
import com.zhangke.notiontodo.config.NotionPageConfigRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PageManagerViewModel : ViewModel() {

    val pageConfigList = MutableLiveData<List<NotionPageConfig>>()

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

    suspend fun delete(page: NotionPageConfig) {
        withContext(Dispatchers.IO) {
            NotionPageConfigRepo.deletePageConfig(listOf(page))
        }
    }
}