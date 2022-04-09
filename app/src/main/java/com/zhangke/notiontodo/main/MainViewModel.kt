package com.zhangke.notiontodo.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zhangke.notiontodo.config.NotionPageConfig

class MainViewModel: ViewModel() {

    val pageList = MutableLiveData<NotionPageConfig>()



}