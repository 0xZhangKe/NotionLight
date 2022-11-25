package com.zhangke.notionlight.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.zhangke.architect.theme.AppMaterialTheme
import com.zhangke.architect.theme.PrimaryText
import com.zhangke.framework.utils.DataWithLoading
import com.zhangke.framework.utils.LoadingState
import com.zhangke.notionlight.R
import com.zhangke.notionlib.data.NotionBlock
import com.zhangke.notionlib.ext.getLightText
import com.zhangke.notionlight.editblock.EditBlockActivity
import kotlinx.coroutines.flow.MutableStateFlow

class PageFragment : Fragment() {

    companion object {

        private const val ARG_PAGE_ID = "arg_page_id"

        fun create(pageId: String): Fragment {
            return PageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PAGE_ID, pageId)
                }
            }
        }
    }

    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var pageId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        pageId = requireArguments().getString(ARG_PAGE_ID)!!

        val blockFlow = viewModel.getPageBlockList(pageId)
        return ComposeView(requireContext()).apply {
            setContent {
                AppMaterialTheme {
                    PageContent(blockFlow)
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PageContent(blockFlow: MutableStateFlow<DataWithLoading<List<NotionBlock>>>) {
        val blockListWithLoading = blockFlow.collectAsState().value
        val state = blockListWithLoading.state
        if (state == LoadingState.IDLE) return
        val refreshing = state == LoadingState.LOADING
        val listState: LazyListState = rememberLazyListState()
        val showBlockColumnDialog = remember { mutableStateOf(false) }
        val currentItem = remember { mutableStateOf<NotionBlock?>(null) }
        if (showBlockColumnDialog.value) {
            BlockColumnDialog(currentItem.value!!) {
                showBlockColumnDialog.value = false
            }
        }
        when (state) {
            LoadingState.FAILED -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.mipmap.empty_paper),
                        modifier = Modifier.size(60.dp, 60.dp),
                        contentDescription = "empty",
                        alpha = 0.4F,
                    )
                }
            }
            else -> {
                SwipeRefresh(
                    state = rememberSwipeRefreshState(isRefreshing = refreshing),
                    onRefresh = { viewModel.refresh(pageId) }
                ) {
                    val blockList = blockListWithLoading.data
                    if (blockList.isNullOrEmpty()) return@SwipeRefresh
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 50.dp, top = 20.dp),
                    ) {
                        items(blockList.size) { index ->
                            val item = blockList[index]
                            Surface(
                                shape = RoundedCornerShape(3.dp),
                                shadowElevation = 2.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(25.dp, 7.dp, 25.dp, 7.dp)
                                    .clickable {
                                        currentItem.value = item
                                        showBlockColumnDialog.value = true
                                    }
                            ) {
                                PrimaryText(
                                    modifier = Modifier.padding(13.dp, 13.dp, 13.dp, 13.dp),
                                    text = item.getLightText().orEmpty()
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun BlockColumnDialog(block: NotionBlock, onDismissRequest: () -> Unit) {
        Dialog(
            onDismissRequest = onDismissRequest
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            ) {

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.openInNotion(pageId)
                            onDismissRequest()
                        }
                        .padding(25.dp, 10.dp, 25.dp, 10.dp)
                ) {
                    PrimaryText(
                        modifier = Modifier.padding(13.dp, 13.dp, 13.dp, 13.dp),
                        text = getString(R.string.block_dialog_open_in_notion)
                    )
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.copy(block)
                            onDismissRequest()
                        }
                        .padding(25.dp, 10.dp, 25.dp, 10.dp)
                ) {
                    PrimaryText(
                        modifier = Modifier.padding(13.dp, 13.dp, 13.dp, 13.dp),
                        text = getString(R.string.copy)
                    )
                }

                if (viewModel.isBlockEditable(block)) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                EditBlockActivity.open(requireActivity(), pageId = pageId, blockId = block.id)
                                onDismissRequest()
                            }
                            .padding(25.dp, 10.dp, 25.dp, 10.dp)
                    ) {
                        PrimaryText(
                            modifier = Modifier.padding(13.dp, 13.dp, 13.dp, 13.dp),
                            text = getString(R.string.edit)
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.delete(block, pageId)
                            onDismissRequest()
                        }
                        .padding(25.dp, 10.dp, 25.dp, 10.dp)
                ) {
                    PrimaryText(
                        modifier = Modifier.padding(13.dp, 13.dp, 13.dp, 13.dp),
                        text = getString(R.string.delete)
                    )
                }
            }
        }
    }
}