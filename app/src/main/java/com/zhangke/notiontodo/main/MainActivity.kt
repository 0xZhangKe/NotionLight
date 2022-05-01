package com.zhangke.notiontodo.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhangke.framework.utils.toast
import com.zhangke.notionlib.data.NotionBlock
import com.zhangke.notionlib.ext.getSimpleText
import com.zhangke.notiontodo.R
import com.zhangke.notiontodo.addblock.AddBlockActivity
import com.zhangke.notiontodo.addpage.AddPageActivity
import com.zhangke.notiontodo.config.NotionPageConfig

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
        var tabIndex by remember { mutableStateOf(0) }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = getString(R.string.app_name),
                            color = Color.Black,
                            fontSize = 18.sp,
                        )
                    },
                    actions = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                painter = rememberVectorPainter(image = Icons.Filled.AccountCircle),
                                contentDescription = "save"
                            )
                        }
                    },
                    backgroundColor = Color.White,
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    modifier = Modifier.padding(end = 20.dp, bottom = 70.dp),
                    onClick = {
                        AddBlockActivity.open(
                            this,
                            vm.pageConfigList.value?.getOrNull(tabIndex)?.id
                        )
                    }) {
                    Icon(
                        painter = rememberVectorPainter(image = Icons.Filled.Add),
                        contentDescription = "ADD"
                    )
                }
            }) {
            val pageConfigListFlow = vm.pageConfigList.collectAsState()
            val pageConfigList = pageConfigListFlow.value
            if (pageConfigList.isNullOrEmpty()) {
                EmptyBlockPage()
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ScrollableTabRow(
                        selectedTabIndex = tabIndex,
                        edgePadding = 0.dp,
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                Modifier.tabIndicatorOffset(tabPositions[tabIndex])
                            )
                        },
                        modifier = Modifier.height(48.dp),
                    ) {
                        pageConfigList.forEachIndexed { index, item ->
                            Tab(
                                modifier = Modifier.fillMaxHeight(),
                                selected = tabIndex == index,
                                onClick = {
                                    tabIndex = index
                                },
                            ) {
                                Text(
                                    text = item.title,
                                    modifier = Modifier.padding(10.dp, 0.dp, 10.dp, 0.dp)
                                )
                            }
                        }
                    }
                    val list =
                        vm.getPageBlockList(pageConfigList[tabIndex].id).collectAsState().value
                    PageContent(blockList = list)
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PageContent(blockList: List<NotionBlock>?) {
        if (blockList.isNullOrEmpty()) return
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 50.dp),
        ) {
            items(blockList.size) { index ->
                val item = blockList[index]
                Surface(
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp, 10.dp, 15.dp, 10.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(13.dp, 13.dp, 13.dp, 13.dp),
                        text = item.childrenBlock?.getSimpleText().orEmpty()
                    )
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
                    AddPageActivity.open(this)
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