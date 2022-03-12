package com.zhangke.notiontodo

import androidx.multidex.MultiDexApplication
import com.zhangke.framework.utils.initApplication

/**
 * Created by ZhangKe on 2022/3/13.
 */
class App: MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        initApplication(this)
    }
}