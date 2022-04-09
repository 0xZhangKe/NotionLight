package com.zhangke.notiontodo.main

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.zhangke.notionlib.NotionRepo
import com.zhangke.notionlib.auth.NotionAuthorization
import com.zhangke.notiontodo.R
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
        findViewById<View>(R.id.oauth).setOnClickListener { NotionAuthorization.showAuthPage() }
        findViewById<View>(R.id.query).setOnClickListener {
            lifecycleScope.launch {
                val pages = try {
                    withContext(Dispatchers.IO) {
                        NotionRepo.queryAllPages()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    null
                }
                if (pages != null) {
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
    }

    private suspend fun updateContent(text: String) = withContext(Dispatchers.Main) {
        findViewById<TextView>(R.id.info).text = text
    }
}