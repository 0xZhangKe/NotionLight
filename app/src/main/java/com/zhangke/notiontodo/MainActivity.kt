package com.zhangke.notiontodo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.zhangke.notionlib.NotionRepo
import com.zhangke.notionlib.auth.NotionAuthorization
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lifecycleScope.launch {
            val oauthToken = NotionAuthorization.getOauthToken()
            if (oauthToken != null) {
                updateContent(oauthToken.toString())
            } else {
                updateContent("no-oauth")
            }
        }
        findViewById<View>(R.id.oauth).setOnClickListener { NotionAuthorization.startAuth() }
        findViewById<View>(R.id.query).setOnClickListener {
            lifecycleScope.launch {
                val pages = withContext(Dispatchers.IO) {
                    NotionRepo.queryAllPages()
                }
                val builder = StringBuilder()
                pages.forEach {
                    builder.appendLine(
                        it.properties?.title?.title?.firstOrNull()?.plainText ?: "null"
                    )
                }
                updateContent(builder.toString())
            }
        }
    }

    private suspend fun updateContent(text: String) = withContext(Dispatchers.Main) {
        findViewById<TextView>(R.id.info).text = text
    }
}