package com.zhangke.notiontodo.pagemanager

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.zhangke.architect.activity.BaseActivity
import com.zhangke.architect.theme.AppMaterialTheme
import com.zhangke.architect.theme.PrimaryText
import com.zhangke.notiontodo.R
import com.zhangke.notiontodo.composable.Toolbar
import kotlinx.coroutines.launch

class PageManagerActivity : BaseActivity() {

    companion object {

        fun open(activity: Activity) {
            Intent(activity, PageManagerActivity::class.java).let {
                activity.startActivity(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: PageManagerViewModel by viewModels()
        setContent {
            AppMaterialTheme {
                Page(viewModel)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Page(viewModel: PageManagerViewModel) {
        val scope = rememberCoroutineScope()
        val listState: LazyListState = rememberLazyListState()
        Scaffold(
            topBar = {
                Toolbar(
                    title = getString(R.string.page_manager_title),
                    navigationBackClick = { finish() },
                    actions = {
                        IconButton(onClick = {
                            AddPageActivity.open(this@PageManagerActivity)
                        }) {
                            Icon(
                                painter = rememberVectorPainter(image = Icons.Rounded.Add),
                                contentDescription = "add new page"
                            )
                        }
                    }
                )
            }) {
            val pageList = viewModel.pageConfigList.observeAsState().value
            if (!pageList.isNullOrEmpty()) {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 25.dp, vertical = 20.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(pageList.size) { index ->
                        Surface(
                            shape = RoundedCornerShape(12.0.dp),
                            shadowElevation = 12.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                                .height(50.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 5.dp)
                            ) {
                                PrimaryText(
                                    text = pageList[index].title,
                                    modifier = Modifier.padding(start = 20.dp)
                                )

                                Spacer(modifier = Modifier.weight(1F))

                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            viewModel.delete(pageList[index])
                                        }
                                    }
                                ) {
                                    Icon(
                                        painter = rememberVectorPainter(image = Icons.Outlined.Delete),
                                        contentDescription = "delete it"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}