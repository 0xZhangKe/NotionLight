package com.zhangke.notiontodo.addblock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhangke.framework.utils.StatusBarUtils
import com.zhangke.notiontodo.R

class AddBlockActivity : ComponentActivity() {

    companion object {

        const val INTENT_ARG_PAGE = "arg_page"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm: AddBlockViewModel by viewModels()
        setContent {
            MaterialTheme {
                PageScreen(vm = vm)
            }
        }
    }

    @Composable
    fun PageScreen(vm: AddBlockViewModel) {
        val statusBarHeight = StatusBarUtils.getStatusBarHeight()
        var inputtedText by remember { mutableStateOf("") }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x99000000)),
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
                    Text(
                        modifier = Modifier.padding(top = 15.dp),
                        text = getString(R.string.add_block_title),
                        fontSize = 18.sp,
                        color = Color.Black,
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp, 15.dp, 20.dp, 0.dp)
                    ) {
                        Text(text = getString(R.string.add_block_item_page_type))
                        Spacer(modifier = Modifier.weight(1F))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {

                            }
                        ) {
                            val pageConfig = vm.currentPage.observeAsState()
                            pageConfig.value?.title?.let {
                                Text(text = it)
                            }
                            Icon(
                                modifier = Modifier.padding(start = 3.dp),
                                painter = rememberVectorPainter(image = Icons.Filled.ArrowDropDown),
                                contentDescription = "Select other"
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp, 15.dp, 20.dp, 0.dp)
                    ) {
                        Text(text = getString(R.string.add_block_item_block_type))
                        Spacer(modifier = Modifier.weight(1F))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {

                            }
                        ) {
                            val blockType = vm.currentBlockType.observeAsState()
                            blockType.value?.let {
                                Text(text = it)
                            }
                            Icon(
                                modifier = Modifier.padding(start = 3.dp),
                                painter = rememberVectorPainter(image = Icons.Filled.ArrowDropDown),
                                contentDescription = "Select other"
                            )
                        }
                    }

                    TextField(
                        value = inputtedText,
                        shape = RoundedCornerShape(6.dp),
                        onValueChange = { inputtedText = it },
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
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(Color.White),
                    onClick = {
                        finish()
                    }
                ) {
                    Text(
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                        text = getString(R.string.cancel)
                    )
                }

                Spacer(modifier = Modifier.weight(1F))

                Button(
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(Color.White),
                    onClick = {

                    }
                ) {
                    Text(
                        color = Color.Black,
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