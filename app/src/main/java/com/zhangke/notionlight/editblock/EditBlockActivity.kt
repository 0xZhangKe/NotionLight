package com.zhangke.notionlight.editblock

import android.app.Activity
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
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhangke.architect.activity.BaseActivity
import com.zhangke.architect.coroutines.collectWithLifecycle
import com.zhangke.architect.theme.AppMaterialTheme
import com.zhangke.architect.theme.PrimaryText
import com.zhangke.framework.utils.StatusBarUtils
import com.zhangke.framework.utils.toast
import com.zhangke.notionlight.R
import com.zhangke.notionlight.composable.AppColor
import com.zhangke.notionlight.support.supportedEditType
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * Supported:
 * - add block
 * - edit block
 * - edit draft
 */
class EditBlockActivity : BaseActivity() {

    companion object {

        const val ACTION_ADD_BLOCK = "com.zhangke.notion.ADD_BLOCK"

        const val INTENT_ARG_PAGE_ID = "intent_arg_page_id"
        const val INTENT_ARG_DRAFT_ID = "intent_arg_draft_id"
        const val INTENT_ARG_BLOCK_ID = "intent_arg_block_id"

        fun open(
            activity: Activity,
            draftId: Long? = null,
            pageId: String? = null,
            blockId: String? = null
        ) {
            val intent = Intent(activity, EditBlockActivity::class.java).apply {
                putExtra(INTENT_ARG_DRAFT_ID, draftId)
                putExtra(INTENT_ARG_PAGE_ID, pageId)
                putExtra(INTENT_ARG_BLOCK_ID, blockId)
            }
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm: EditBlockViewModel by viewModels()
        vm.parseIntent(intent)
        val viewStateCombiner = vm.viewStatesCombiner
        setContent {
            AppMaterialTheme {
                ApplyBlockPage(
                    viewStateCombiner,
                    onContentChanged = { vm.onInputContentChanged(it) },
                    onConfirmClick = { vm.onConfirm() },
                    onCancelClick = { finish() },
                    onPageSelected = { vm.onPageSelected(it) },
                    onBlockTypeSelected = { vm.onBlockTypeChanged(it) }
                )
            }
        }

        viewStateCombiner
            .applyBlockState
            .collectWithLifecycle(this) {
                toast(it.second)
                if (it.first) {
                    finish()
                }
            }
    }

    @Composable
    fun ApplyBlockPage(
        viewStatesCombiner: NotionBlockViewStatesCombiner,
        onContentChanged: (String) -> Unit,
        onCancelClick: () -> Unit,
        onConfirmClick: () -> Unit,
        onPageSelected: (page: EditBlockViewModel.NotionPage) -> Unit,
        onBlockTypeSelected: (blockType: String) -> Unit
    ) {
        val statusBarHeight by remember { mutableStateOf(StatusBarUtils.getStatusBarHeight()) }
        val currentBlockContent by viewStatesCombiner.content.collectAsState(initial = "")
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColor.translucentBackground)
                .clickable { onCancelClick() },
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, statusBarHeight + 20.dp, 20.dp, 0.dp)
                    .clickable {  }
                    .weight(2F),
                shape = RoundedCornerShape(18.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    PrimaryText(
                        modifier = Modifier.padding(top = 15.dp),
                        text = viewStatesCombiner.title.collectAsState("").value,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    PageSelector(
                        pageList = viewStatesCombiner.pageList.collectAsState(initial = emptyList()).value,
                        currentPage = viewStatesCombiner.currentPage.collectAsState(initial = null).value,
                        canEditPage = viewStatesCombiner.canEditPage.collectAsState(initial = false).value,
                        onPageSelected = onPageSelected
                    )

                    BlockTypeSelector(
                        blockTypeList = viewStatesCombiner.blockTypeList,
                        currentBlockType = viewStatesCombiner.currentBlockType
                            .collectAsState(initial = null).value,
                        onBlockTypeSelected = onBlockTypeSelected
                    )

                    TextField(
                        value = currentBlockContent,
                        shape = RoundedCornerShape(6.dp),
                        onValueChange = onContentChanged,
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color(0x99CCCCCC),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp, 20.dp, 20.dp, 20.dp),
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
                    onClick = onCancelClick
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
                    onClick = onConfirmClick
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

    @Composable
    fun PageSelector(
        pageList: List<EditBlockViewModel.NotionPage>,
        currentPage: EditBlockViewModel.NotionPage?,
        canEditPage: Boolean,
        onPageSelected: (page: EditBlockViewModel.NotionPage) -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 15.dp, 20.dp, 0.dp)
        ) {
            PrimaryText(text = getString(R.string.add_block_item_page_type))
            Spacer(modifier = Modifier.weight(1F))

            if (currentPage != null) {
                if (canEditPage) {
                    var pageTypeExpanded by remember { mutableStateOf(false) }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                pageTypeExpanded = true
                            }
                        ) {
                            PrimaryText(text = currentPage.pageName)
                            androidx.compose.material3.Icon(
                                modifier = Modifier.padding(start = 3.dp),
                                painter = rememberVectorPainter(image = Icons.Filled.ArrowDropDown),
                                contentDescription = "Select other"
                            )
                        }
                        DropdownMenu(
                            expanded = pageTypeExpanded,
                            onDismissRequest = { pageTypeExpanded = false }) {
                            pageList.forEach {
                                DropdownMenuItem(onClick = {
                                    pageTypeExpanded = false
                                    onPageSelected(it)
                                }) {
                                    PrimaryText(text = it.pageName)
                                }
                            }
                        }
                    }
                } else {
                    PrimaryText(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = currentPage.pageName
                    )
                }
            }
        }
    }

    @Composable
    fun BlockTypeSelector(
        blockTypeList: Array<String>,
        currentBlockType: String?,
        onBlockTypeSelected: (blockType: String) -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 15.dp, 20.dp, 0.dp)
        ) {
            PrimaryText(text = getString(R.string.add_block_item_block_type))
            Spacer(modifier = Modifier.weight(1F))

            if (currentBlockType != null) {
                var blockTypeExpanded by remember { mutableStateOf(false) }
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            blockTypeExpanded = true
                        }
                    ) {
                        PrimaryText(text = currentBlockType)
                        androidx.compose.material3.Icon(
                            modifier = Modifier.padding(start = 3.dp),
                            painter = rememberVectorPainter(image = Icons.Filled.ArrowDropDown),
                            contentDescription = "Select other content type"
                        )
                    }
                    DropdownMenu(
                        expanded = blockTypeExpanded,
                        onDismissRequest = { blockTypeExpanded = false }) {
                        blockTypeList.forEach {
                            DropdownMenuItem(onClick = {
                                blockTypeExpanded = false
                                onBlockTypeSelected(it)
                            }) {
                                PrimaryText(text = it)
                            }
                        }
                    }
                }
            }
        }
    }
}

class NotionBlockViewStatesCombiner(
    val title: MutableSharedFlow<String> = MutableSharedFlow(1),
    val currentPage: MutableSharedFlow<EditBlockViewModel.NotionPage> = MutableSharedFlow(1),
    val pageList: MutableSharedFlow<List<EditBlockViewModel.NotionPage>> = MutableSharedFlow(1),
    val canEditPage: MutableSharedFlow<Boolean> = MutableSharedFlow(1),
    val blockTypeList: Array<String> = supportedEditType,
    val currentBlockType: MutableSharedFlow<String> = MutableSharedFlow(1),
    val content: MutableSharedFlow<String> = MutableSharedFlow(1),
    val savingState: MutableSharedFlow<String> = MutableSharedFlow(1),
    val applyBlockState: MutableSharedFlow<Pair<Boolean, String>> = MutableSharedFlow(1),
)