package com.zhangke.notiontodo.editblock

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhangke.architect.activity.BaseActivity
import com.zhangke.architect.theme.AppMaterialTheme
import com.zhangke.architect.theme.PrimaryText
import com.zhangke.framework.utils.StatusBarUtils
import com.zhangke.framework.utils.toast
import com.zhangke.notionlib.data.NotionBlock
import com.zhangke.notionlib.ext.getLightText
import com.zhangke.notiontodo.R
import com.zhangke.notiontodo.composable.AppColor

class EditBlockActivity : BaseActivity() {

    companion object {

        const val INTENT_ARG_PAGE_ID = "intent_arg_page_id"
        const val INTENT_ARG_BLOCK_ID = "intent_arg_block_id"

        fun open(activity: Activity, pageId: String, block: NotionBlock) {
            val intent = Intent(activity, EditBlockActivity::class.java).apply {
                putExtra(INTENT_ARG_PAGE_ID, pageId)
                putExtra(INTENT_ARG_BLOCK_ID, block.id)
            }
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm: EditBlockViewModel by viewModels()
        vm.parseIntent(intent)
        setContent {
            AppMaterialTheme {
                PageScreen(vm = vm)
            }
        }
        vm.onAddSuccess = {
            toast(R.string.edit_block_success)
            finish()
        }
    }

    @Composable
    fun PageScreen(vm: EditBlockViewModel) {
        val statusBarHeight by remember { mutableStateOf(StatusBarUtils.getStatusBarHeight()) }
        val originText = vm.block.observeAsState().value?.getLightText().orEmpty()
        var inputtedText by remember { mutableStateOf("") }
        val textFieldText = inputtedText.ifEmpty { originText }
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
                        text = getString(R.string.edit_block_page_title),
                        fontSize = 18.sp
                    )

                    TextField(
                        value = textFieldText,
                        shape = RoundedCornerShape(6.dp),
                        onValueChange = {
                            inputtedText = it
                        },
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
                        vm.update(inputtedText)
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