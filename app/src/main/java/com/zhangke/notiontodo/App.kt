package com.zhangke.notiontodo

import androidx.multidex.MultiDexApplication
import com.zhangke.framework.utils.initApplication
import com.zhangke.notionlib.NotionLibStartup
import com.zhangke.architect.daynight.DayNightHelper

/**
 * Created by ZhangKe on 2022/3/13.
 */
class App: MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        initApplication(this)
        DayNightHelper
        NotionLibStartup.start()
    }
}