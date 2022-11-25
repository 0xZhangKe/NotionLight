package com.zhangke.notionlight.draft

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.zhangke.architect.theme.AppMaterialTheme
import com.zhangke.architect.theme.PrimaryText
import com.zhangke.notionlight.R
import com.zhangke.notionlight.composable.Toolbar
import com.zhangke.notionlight.draft.db.DraftEntry
import com.zhangke.notionlight.editblock.EditBlockActivity

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
                    onClearDraft = viewModel::clearDraftBox,
                    onEditDraft = {
                        EditBlockActivity.open(
                            this,
                            pageId = it.pageId,
                            draftId = it.draftId
                        )
                    },
                    onDeleteDraft = { viewModel.deleteDraft(it) }
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DraftBoxManagerPage(
        allDrafts: List<DraftEntry>,
        onClearDraft: () -> Unit,
        onEditDraft: (draft: DraftEntry) -> Unit,
        onDeleteDraft: (draft: DraftEntry) -> Unit,
    ) {
        var showClearDialog by remember { mutableStateOf(false) }
        Scaffold(
            topBar = {
                Toolbar(
                    title = getString(R.string.draft_page_title),
                    navigationBackClick = {
                        finish()
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                showClearDialog = true
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_clear),
                                modifier = Modifier.size(24.dp),
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
                    Draft(
                        draft = item,
                        onDelete = { onDeleteDraft(item) },
                        onEdit = { onEditDraft(item) }
                    )
                }
            }
        }
        if (showClearDialog) {
            ClearDialog(
                onDismissRequest = { showClearDialog = false },
                onClearClick = onClearDraft
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Draft(draft: DraftEntry, onDelete: () -> Unit, onEdit: () -> Unit) {
        var showControlPanel by remember { mutableStateOf(false) }
        Surface(
            shape = RoundedCornerShape(3.dp),
            shadowElevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 15.dp, end = 20.dp)
                .clickable { showControlPanel = true }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                PrimaryText(
                    text = draft.content,
                    modifier = Modifier
                        .padding(top = 5.dp, start = 10.dp, end = 10.dp, bottom = 10.dp)
                        .fillMaxWidth(),
                    fontSize = 16.sp
                )
            }
        }
        if (showControlPanel) {
            ControlPanel(
                onDismissRequest = { showControlPanel = false },
                onDelete = onDelete,
                onEdit = onEdit
            )
        }
    }

    @Composable
    fun ClearDialog(onDismissRequest: () -> Unit, onClearClick: () -> Unit) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                Button(onClick = {
                    onDismissRequest()
                    onClearClick()
                }) {
                    Text(text = resources.getString(R.string.ok))
                }
            },
            dismissButton = {
                Button(onClick = onDismissRequest) {
                    Text(text = resources.getString(R.string.cancel))
                }
            },
            title = {
                Text(text = resources.getString(R.string.draft_clear_dialog_title))
            }
        )
    }

    @Composable
    fun ControlPanel(
        onDismissRequest: () -> Unit,
        onDelete: () -> Unit,
        onEdit: () -> Unit
    ) {
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
                            onEdit()
                            onDismissRequest()
                        }
                        .padding(25.dp, 10.dp, 25.dp, 10.dp)
                ) {
                    PrimaryText(
                        modifier = Modifier.padding(13.dp, 13.dp, 13.dp, 13.dp),
                        text = getString(R.string.edit)
                    )
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onDelete()
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