package com.zhangke.notionlib.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.zhangke.notionlib.NotionRepo
import kotlinx.coroutines.launch

class AuthorizationActivity : ComponentActivity() {

    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        textView = TextView(this)
        textView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textView.setPadding(30, 30, 30, 30)
        setContentView(textView)
        intent?.let { handleIntent(it) }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }

    @SuppressLint("SetTextI18n")
    private fun handleIntent(intent: Intent) {
        val data = intent.data
        if (data == null) {
            Log.d("B_TEST", "data is null")
            return
        }
        val code = data.query?.replaceFirst("code=", "")
        Log.d("B_TEST", "code=${code}")
        if (code == null) {
            textView.text = "${textView.text}\nerror."
        } else {
            textView.text = "${textView.text}\ncode=$code"
            startAuthWithCode(code)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun startAuthWithCode(code: String) {
        textView.text = "${textView.text}\nauthing..."
        lifecycleScope.launch {
            try {
                val token = NotionRepo.getOauthToken(code)
                println(token)
                textView.text = "${textView.text}\n$token"
            } catch (e: Exception) {
                e.printStackTrace()
                textView.text = "${textView.text}\n${e.message}\n${e.stackTraceToString()}"
            }
        }
    }
}