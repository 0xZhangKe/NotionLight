package com.zhangke.notionlight.addblock

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhangke.architect.activity.BaseActivity
import com.zhangke.architect.theme.AppMaterialTheme
import com.zhangke.architect.theme.PrimaryText
import com.zhangke.framework.utils.StatusBarUtils
import com.zhangke.framework.utils.toast
import com.zhangke.notionlight.R
import com.zhangke.notionlight.composable.AppColor

class AddBlockActivity : BaseActivity() {

    companion object {

        const val ACTION_ADD_BLOCK = "com.zhangke.notion.ADD_BLOCK"
        const val INTENT_ARG_PAGE = "arg_page"

        fun open(content: Context, pageId: String? = null) {
            Intent(content, AddBlockActivity::class.java).let {
                it.putExtra(INTENT_ARG_PAGE, pageId)
                content.startActivity(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm: AddBlockViewModel by viewModels()
        setContent {
            AppMaterialTheme {
                PageScreen(vm = vm)
            }
        }
        vm.parseIntent(intent)
        vm.onAddSuccess = {
            toast(R.string.add_block_success)
            finish()
        }
    }

    @Composable
    fun PageScreen(vm: AddBlockViewModel) {
        val statusBarHeight = StatusBarUtils.getStatusBarHeight()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColor.translucentBackground),
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, statusBarHeight + 20.dp, 20.dp, 0.dp)
                    .weight(2F),
                shape = RoundedCornerShape(18.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    PrimaryText(
                        modifier = Modifier.padding(top = 15.dp),
                        text = getString(R.string.add_block_title),
                        fontSize = 18.sp
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp, 15.dp, 20.dp, 0.dp)
                    ) {
                        PrimaryText(text = getString(R.string.add_block_item_page_type))
                        Spacer(modifier = Modifier.weight(1F))

                        var pageTypeExpanded by remember { mutableStateOf(false) }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    pageTypeExpanded = true
                                }
                            ) {
                                val pageConfig = vm.currentPage.observeAsState()
                                pageConfig.value?.title?.let {
                                    PrimaryText(text = it)
                                }
                                Icon(
                                    modifier = Modifier.padding(start = 3.dp),
                                    painter = rememberVectorPainter(image = Icons.Filled.ArrowDropDown),
                                    contentDescription = "Select other"
                                )
                            }
                            DropdownMenu(
                                expanded = pageTypeExpanded,
                                onDismissRequest = { pageTypeExpanded = false }) {
                                vm.pageList?.forEach {
                                    DropdownMenuItem(onClick = {
                                        pageTypeExpanded = false
                                        vm.currentPage.value = it
                                    }) {
                                        PrimaryText(text = it.title)
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp, 15.dp, 20.dp, 0.dp)
                    ) {
                        PrimaryText(text = getString(R.string.add_block_item_block_type))
                        Spacer(modifier = Modifier.weight(1F))

                        var blockTypeExpanded by remember { mutableStateOf(false) }
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    blockTypeExpanded = true
                                }
                            ) {
                                val blockType = vm.currentBlockType.observeAsState()
                                blockType.value?.let {
                                    PrimaryText(text = it)
                                }
                                Icon(
                                    modifier = Modifier.padding(start = 3.dp),
                                    painter = rememberVectorPainter(image = Icons.Filled.ArrowDropDown),
                                    contentDescription = "Select other"
                                )
                            }
                            DropdownMenu(
                                expanded = blockTypeExpanded,
                                onDismissRequest = { blockTypeExpanded = false }) {
                                vm.blockTypeList.forEach {
                                    DropdownMenuItem(onClick = {
                                        blockTypeExpanded = false
                                        vm.currentBlockType.value = it
                                    }) {
                                        PrimaryText(text = it)
                                    }
                                }
                            }
                        }
                    }

                    val inputtedText = vm.currentInputText.observeAsState("")

                    TextField(
                        value = inputtedText.value,
                        shape = RoundedCornerShape(6.dp),
                        onValueChange = { vm.currentInputText.value = it },
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color(0x99CCCCCC),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp, 20.dp, 20.dp, 20.dp)
                            .defaultMinSize(minHeight = 80.dp),
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 10.dp, 20.dp, 0.dp)
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface),
                    shape = RoundedCornerShape(30.dp),
                    onClick = {
                        finish()
                    }
                ) {
                    PrimaryText(
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                        text = getString(R.string.cancel)
                    )
                }

                Spacer(modifier = Modifier.weight(1F))

                Button(
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface),
                    shape = RoundedCornerShape(30.dp),
                    onClick = {
                        vm.saveContent()
                    }
                ) {
                    PrimaryText(
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                        text = getString(R.string.ok)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(3F))
        }
    }
}