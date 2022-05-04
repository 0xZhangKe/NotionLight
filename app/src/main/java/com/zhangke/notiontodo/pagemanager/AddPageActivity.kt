package com.zhangke.notiontodo.pagemanager

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.zhangke.architect.activity.BaseActivity
import com.zhangke.framework.utils.toast
import com.zhangke.notiontodo.R
import com.zhangke.architect.theme.AppMaterialTheme
import com.zhangke.notiontodo.composable.PageLoading
import com.zhangke.architect.theme.PrimaryText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddPageActivity : BaseActivity() {

    private val vm: AddPageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppMaterialTheme {
                PageScreen(vm)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        vm.loadPage()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PageScreen(vm: AddPageViewModel) {
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
                                    vm.savePage()
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
                    backgroundColor = MaterialTheme.colorScheme.background,
                    title = {
                        PrimaryText(
                            text = getString(R.string.add_page_title),
                            fontSize = 18.sp,
                        )
                    },
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
                        val page = item.page
                        var checked: Boolean by remember {
                            mutableStateOf(item.added)
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
                                Checkbox(
                                    checked = checked,
                                    onCheckedChange = {
                                        checked = it
                                        item.added = checked
                                    })
                                PrimaryText(
                                    text = item.title
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {

        fun open(activity: Activity) {
            Intent(activity, AddPageActivity::class.java).let {
                activity.startActivity(it)
            }
        }
    }
}