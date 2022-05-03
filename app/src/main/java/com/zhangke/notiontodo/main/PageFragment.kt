package com.zhangke.notiontodo.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.zhangke.framework.utils.DataWithLoading
import com.zhangke.framework.utils.LoadingState
import com.zhangke.notionlib.data.NotionBlock
import com.zhangke.notionlib.ext.getSimpleText
import com.zhangke.notiontodo.R
import com.zhangke.notiontodo.composable.AppMaterialTheme
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
        val refreshing = state == LoadingState.LOADING
        val listState: LazyListState = rememberLazyListState()
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
            }
        }
    }
}