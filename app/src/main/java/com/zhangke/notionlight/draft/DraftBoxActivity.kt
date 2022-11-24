package com.zhangke.notionlight.draft

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.zhangke.architect.theme.AppMaterialTheme
import com.zhangke.architect.theme.PrimaryText
import com.zhangke.architect.theme.SecondaryText
import com.zhangke.framework.utils.toast
import com.zhangke.notionlight.R
import com.zhangke.notionlight.composable.PageLoading
import com.zhangke.notionlight.composable.Toolbar
import com.zhangke.notionlight.draft.db.DraftEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DraftBoxActivity : AppCompatActivity() {

    companion object {

        fun open(activity: Activity) {
            activity.startActivity(Intent(activity, DraftBoxActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: DraftBoxViewModel by viewModels()

        setContent {
            AppMaterialTheme {
                DraftBoxManagerPage(
                    allDrafts = viewModel.allDrafts.collectAsState(initial = emptyList()).value,
                    onClearDraft = viewModel::clearDraftBox
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DraftBoxManagerPage(allDrafts: List<DraftEntry>, onClearDraft: () -> Unit) {
        Scaffold(
            topBar = {
                Toolbar(
                    title = getString(R.string.add_page_title),
                    navigationBackClick = {
                        finish()
                    },
                    actions = {
                        IconButton(
                            onClick = onClearDraft
                        ) {
                            Icon(
                                painter = rememberVectorPainter(image = Icons.Filled.ClearAll),
                                contentDescription = "save"
                            )
                        }
                    }
                )
            }) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 30.dp)
            ) {
                items(allDrafts) { item ->
                    Draft(item)
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Draft(draft: DraftEntry) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 15.dp, end = 20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                PrimaryText(
                    text = "${draft.blockType}: ${draft.date}",
                    modifier = Modifier
                        .padding(top = 15.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )

                SecondaryText(
                    text = draft.content,
                    modifier = Modifier.padding(top = 15.dp),
                    maxLines = 1
                )
            }
        }
    }
}