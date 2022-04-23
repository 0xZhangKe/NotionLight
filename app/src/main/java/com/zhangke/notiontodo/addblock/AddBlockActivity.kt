package com.zhangke.notiontodo.addblock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.TextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhangke.framework.utils.StatusBarUtils
import com.zhangke.notiontodo.R

class AddBlockActivity : ComponentActivity() {


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
                shape = RoundedCornerShape(20),
                modifier = Modifier
                    .padding(20.dp, statusBarHeight + 20.dp, 20.dp, 20.dp)
                    .background(Color.White)
                    .weight(2F)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        modifier = Modifier.padding(top = 20.dp),
                        text = getString(R.string.add_block_title),
                        fontSize = 18.sp,
                        color = Color.Black,
                    )

                    TextField(
                        value = inputtedText,
                        onValueChange = { inputtedText = it },
                        modifier = Modifier
                            .padding(20.dp, 20.dp, 20.dp, 20.dp)
                            .fillMaxWidth(),
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 20.dp, 20.dp, 0.dp)
            ) {
                Button(
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier
                        .height(30.dp)
                        .padding(start = 10.dp, end = 10.dp),
                    onClick = {

                    }
                ) {
                    Text(
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        text = getString(R.string.cancel)
                    )
                }

                Spacer(modifier = Modifier.weight(1F))

                Button(
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier
                        .height(30.dp)
                        .padding(start = 10.dp, end = 10.dp),
                    onClick = {

                    }
                ) {
                    Text(
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        text = getString(R.string.ok)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(3F))
        }
    }
}