package com.zhangke.notiontodo.pagemanager

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhangke.notiontodo.R
import com.zhangke.notiontodo.composable.AppMaterialTheme
import kotlinx.coroutines.launch

class PageManagerActivity : ComponentActivity() {

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
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { finish() }) {
                            Icon(
                                painter = rememberVectorPainter(image = Icons.Filled.ArrowBack),
                                "back"
                            )
                        }
                    },
                    title = {
                        Text(
                            text = getString(R.string.page_manager_title),
                            color = Color.Black,
                            fontSize = 18.sp,
                        )
                    },
                    actions = {
                        IconButton(onClick = {
                            AddPageActivity.open(this@PageManagerActivity)
                        }) {
                            Icon(
                                painter = rememberVectorPainter(image = Icons.Rounded.Add),
                                contentDescription = "add new page"
                            )
                        }
                    },
                    backgroundColor = MaterialTheme.colorScheme.surface
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
                        Card(
                            elevation = CardDefaults.cardElevation(7.dp),
                            containerColor = Color.White,
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
                                Text(
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