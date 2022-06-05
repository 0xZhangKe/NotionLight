package com.zhangke.notionlight.setting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.zhangke.architect.activity.BaseActivity
import com.zhangke.architect.daynight.DayNightHelper
import com.zhangke.architect.language.LanguageHelper
import com.zhangke.architect.language.LanguageSettingType
import com.zhangke.architect.theme.AppMaterialTheme
import com.zhangke.notionlight.R
import com.zhangke.notionlight.composable.Toolbar

class HelpActivity : BaseActivity() {

    companion object {

        fun open(activity: Activity) {
            activity.startActivity(Intent(activity, HelpActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppMaterialTheme {
                HelpPage(readHtmlFromAssets())
            }
        }
    }

    private fun readHtmlFromAssets(): String {
        val assetsName = when (LanguageHelper.currentType) {
            LanguageSettingType.CN -> "help.html"
            else -> "help-en.html"
        }
        return try {
            String(assets.open(assetsName).readBytes())
        } catch (e: Exception) {
            Log.e("HelpActivity", "load help.html error.", e)
            ""
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HelpPage(html: String) {
        Scaffold(
            topBar = {
                Toolbar(
                    title = getString(R.string.helper_page_title),
                    navigationBackClick = {
                        finish()
                    }
                )
            }) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                factory = { context ->
                    TextView(context).apply {
                        val color = if (DayNightHelper.isNight()) {
                            resources.getColor(R.color.text_color_primary_night)
                        } else {
                            resources.getColor(R.color.text_color_primary_day)
                        }
                        setTextColor(color)
                    }
                },
                update = { it.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT) }
            )
        }
    }
}