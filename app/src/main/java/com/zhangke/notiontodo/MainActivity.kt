package com.zhangke.notiontodo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zhangke.notionlib.auth.NotionAuthorization

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        NotionAuthorization.startAuth(this)
    }
}