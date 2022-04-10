package com.zhangke.notiontodo.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.asLiveData
import com.zhangke.framework.utils.toast
import com.zhangke.notiontodo.R
import com.zhangke.notiontodo.config.NotionPageConfig
import kotlinx.coroutines.flow.asSharedFlow

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm: MainViewModel by viewModels()
        setContent {
            MaterialTheme {
                PageScreen(vm)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PageScreen(vm: MainViewModel) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = getString(R.string.app_name),
                                color = Color.Black,
                                fontSize = 18.sp,
                            )
                            Spacer(modifier = Modifier.weight(1F))
                            Image(
                                modifier = Modifier
                                    .size(30.dp)
                                    .offset(x = (-8).dp)
                                    .clickable(
                                        enabled = true,
                                        role = Role.Image,
                                    ) {
                                        toast("icon")
                                    },
                                painter = rememberVectorPainter(image = Icons.Filled.AccountCircle),
                                contentDescription = "user-icon"
                            )
                        }
                    },
                    backgroundColor = Color.White,
                )
            }) {
            val pageConfigListFlow = vm.pageConfigList.collectAsState()
            val pageConfigList = pageConfigListFlow.value
            if (pageConfigList.isNullOrEmpty()) {
                EmptyBlockPage()
            } else {
                ToolbarTab(items = pageConfigList)
            }
        }
    }

    @Composable
    fun ToolbarTab(items: List<NotionPageConfig>) {
        var tabIndex by remember { mutableStateOf(0) }
        Column {
            ScrollableTabRow(
                selectedTabIndex = tabIndex,
                edgePadding = 0.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[tabIndex])
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            ) {
                items.forEachIndexed { index, item ->
                    Tab(
                        modifier = Modifier.fillMaxHeight(),
                        selected = tabIndex == index,
                        onClick = {
                            tabIndex = index
                        },
                    ) {
                        Text(
                            text = item.title,
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun EmptyBlockPage() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    enabled = true,
                    role = Role.Button,
                ) {
                    toast("add")
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Image(
                painter = rememberVectorPainter(image = Icons.Filled.Add),
                contentDescription = "empty-icon",
                modifier = Modifier.size(80.dp),
            )

            Text(text = getString(R.string.add_page_guid))
        }
    }
}