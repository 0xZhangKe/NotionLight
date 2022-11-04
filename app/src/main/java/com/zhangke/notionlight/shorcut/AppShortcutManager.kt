package com.zhangke.notionlight.shorcut

import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.zhangke.architect.coroutines.ApplicationScope
import com.zhangke.framework.utils.appContext
import com.zhangke.notionlight.R
import com.zhangke.notionlight.config.NotionPageConfig
import com.zhangke.notionlight.config.NotionPageConfigRepo
import com.zhangke.notionlight.editblock.EditBlockActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object AppShortcutManager {

    init {
        ApplicationScope.launch {
            ShortcutManagerCompat.removeAllDynamicShortcuts(appContext)
            val pageList = withContext(Dispatchers.IO) {
                NotionPageConfigRepo.getPageConfigList()
                    .firstOrNull()
                    ?.take(5)
            }
            pageList?.map { it.buildShortcut() }
                ?.forEach {
                    ShortcutManagerCompat.pushDynamicShortcut(appContext, it)
                }
        }
    }

    private fun NotionPageConfig.buildShortcut(): ShortcutInfoCompat {
        val intent = Intent().apply {
            action = EditBlockActivity.ACTION_ADD_BLOCK
            setClass(appContext, EditBlockActivity::class.java)
            putExtra(EditBlockActivity.INTENT_ARG_PAGE_ID, id)
        }
        return ShortcutInfoCompat.Builder(appContext, id)
            .setShortLabel(title)
            .setLongLabel(title)
            .setIntent(intent)
            .setIcon(IconCompat.createWithResource(appContext, R.mipmap.ic_launcher))
            .build()
    }
}