package com.zhangke.notiontodo.addpage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.zhangke.framework.utils.toast
import com.zhangke.notionlib.data.NotionPage
import com.zhangke.notionlib.ext.getSimpleText
import com.zhangke.notiontodo.R
import com.zhangke.notiontodo.composable.PageLoading
import com.zhangke.notiontodo.config.NotionPageConfig
import com.zhangke.notiontodo.config.NotionPageType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddPageActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm: AddPageViewModel by viewModels()
        setContent {
            MaterialTheme {
                PageScreen(vm)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PageScreen(vm: AddPageViewModel) {
        val selectedPageList = mutableListOf<NotionPageConfig>()
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
                    actions = {
                        IconButton(
                            onClick = {
                                lifecycleScope.launch {
                                    vm.savePage(selectedPageList)
                                    withContext(Dispatchers.Main) {
                                        toast(R.string.page_add_success)
                                        finish()
                                    }
                                }
                            }
                        ) {
                            Icon(
                                painter = rememberVectorPainter(image = Icons.Filled.Save),
                                contentDescription = "save"
                            )
                        }
                    },
                    title = {
                        Text(
                            text = getString(R.string.add_page_title),
                            color = Color.Black,
                            fontSize = 18.sp,
                        )
                    },
                    backgroundColor = Color.White,
                )
            }) {
            val loading = vm.loading.observeAsState(true)
            if (loading.value) {
                PageLoading()
            }
            val list = vm.notionPageList.collectAsState().value
            if (!list.isNullOrEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(list.size) { index ->
                        val item = list[index]
                        val pageConfig = item.convertToPageConfig()
                        var checked: Boolean by remember {
                            val defaultChecked = selectedPageList.indexOf(pageConfig) != -1
                            mutableStateOf(defaultChecked)
                        }
                        Surface(
                            contentColor = Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { checked = !checked }
                                .padding(10.dp, 5.dp, 10.dp, 5.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Checkbox(checked = checked, onCheckedChange = { checked = it })
                                Text(
                                    text = item.getTitle(),
                                    color = Color.Black
                                )
                            }
                        }
                        if (checked) {
                            selectedPageList += pageConfig
                        } else {
                            selectedPageList -= pageConfig
                        }
                    }
                }
            }
        }
    }

    private fun NotionPage.getTitle(): String {
        return properties?.title?.title?.getSimpleText().orEmpty()
    }

    private fun NotionPage.convertToPageConfig(): NotionPageConfig {
        return NotionPageConfig(
            id = id,
            title = getTitle(),
            type = NotionPageType.CALLOUT
        )
    }

    companion object {

        fun open(activity: Activity) {
            Intent(activity, AddPageActivity::class.java).let {
                activity.startActivity(it)
            }
        }
    }
}