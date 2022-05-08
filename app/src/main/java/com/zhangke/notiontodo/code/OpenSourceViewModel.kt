package com.zhangke.notiontodo.code

import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zhangke.framework.utils.appContext
import com.zhangke.notiontodo.R
import com.zhangke.notiontodo.utils.IntentUtils

class OpenSourceViewModel : ViewModel() {

    val thisOpenSourceInfo = MutableLiveData<ThisProjectOpenSourceInfo>()

    val openSourceInfoList = MutableLiveData<List<OpenSourceInfo>>()

    init {
        thisOpenSourceInfo.value = buildThisProjectInfo()
        openSourceInfoList.value = buildOpenSourceCodeInfo()
    }

    fun openBrowser(activity: Activity, url: String) {
        IntentUtils.openExternalPage(activity, url)
    }

    private fun buildThisProjectInfo(): ThisProjectOpenSourceInfo {
        return ThisProjectOpenSourceInfo(
            icon = AppCompatResources.getDrawable(appContext, R.mipmap.ic_launcher)
                ?: ColorDrawable(appContext.resources.getColor(R.color.primary_day)),
            name = appContext.getString(R.string.app_name),
            author = "ZhangKe",
            license = "The Apache Software License, Version 2.0",
            url = "https://github.com/0xZhangKe/NotionTodo",
        )
    }

    private fun buildOpenSourceCodeInfo(): List<OpenSourceInfo> {
        return listOf(
            OpenSourceInfo(
                name = "Kotlin",
                author = "Jetbrains",
                license = "The Apache Software License, Version 2.0",
                url = "https://kotlinlang.org/"
            ),
            OpenSourceInfo(
                name = "Compose",
                author = "Google",
                license = "The Apache Software License, Version 2.0",
                url = "https://developer.android.com/jetpack/androidx/releases/compose"
            ),
            OpenSourceInfo(
                name = "Jetpack",
                author = "Google",
                license = "The Apache Software License, Version 2.0",
                url = "https://developer.android.com/jetpack"
            ),
            OpenSourceInfo(
                name = "AndroidX",
                author = "Google",
                license = "The Apache Software License, Version 2.0",
                url = "https://developer.android.com/jetpack/androidx/"
            ),
            OpenSourceInfo(
                name = "RxJava",
                author = "ReactiveX",
                license = "The Apache Software License, Version 2.0",
                url = "https://github.com/ReactiveX/RxJava"
            ),
            OpenSourceInfo(
                name = "Gson",
                author = "Google",
                license = "The Apache Software License, Version 2.0",
                url = "https://github.com/google/gson/"
            ),
            OpenSourceInfo(
                name = "OkHttp",
                author = "Square",
                license = "The Apache Software License, Version 2.0",
                url = "https://square.github.io/okhttp/"
            ),
            OpenSourceInfo(
                name = "Retrofit",
                author = "Square",
                license = "The Apache Software License, Version 2.0",
                url = "https://github.com/square/retrofit"
            ),
            OpenSourceInfo(
                name = "Accompanist",
                author = "Google",
                license = "The Apache Software License, Version 2.0",
                url = "https://github.com/google/accompanist"
            ),
            OpenSourceInfo(
                name = "Material-Components",
                author = "Google",
                license = "The Apache Software License, Version 2.0",
                url = "https://github.com/material-components/material-components-android"
            ),
            OpenSourceInfo(
                name = "Coil",
                author = "Coil",
                license = "The Apache Software License, Version 2.0",
                url = "https://github.com/coil-kt/coil"
            ),
        )
    }

    data class ThisProjectOpenSourceInfo(
        val icon: Drawable,
        val name: String,
        val author: String,
        val license: String,
        val url: String,
    )

    data class OpenSourceInfo(
        val name: String,
        val author: String,
        val license: String,
        val url: String,
    )
}