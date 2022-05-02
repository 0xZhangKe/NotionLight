package com.zhangke.notiontodo.setting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradient
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.zhangke.notiontodo.R

class SettingActivity : ComponentActivity() {

    companion object {

        fun open(activity: Activity) {
            Intent(activity, SettingActivity::class.java).let {
                activity.startActivity(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm: SettingViewModel by viewModels()
        setContent {
            MaterialTheme {
                Page(vm)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Page(vm: SettingViewModel) {
        Scaffold(
//            topBar = {
//                TopAppBar(
//                    navigationIcon = {
//                        IconButton(onClick = { finish() }) {
//                            Icon(
//                                painter = rememberVectorPainter(image = Icons.Filled.ArrowBack),
//                                "back"
//                            )
//                        }
//                    },
//                    title = {
//                        Text(
//                            text = getString(R.string.setting_page_title),
//                            color = Color.Black,
//                            fontSize = 18.sp,
//                        )
//                    },
//                    backgroundColor = Color.White,
//                )
//            }
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp, top = 15.dp, end = 15.dp)
            ) {
                IconButton(
                    onClick = { finish() }
                ) {
                    Icon(
                        painter = rememberVectorPainter(image = Icons.Filled.ArrowBack),
                        "back"
                    )
                }

                Spacer(modifier = Modifier.weight(1F))

                IconButton(
                    onClick = { finish() }
                ) {
                    Icon(
                        painter = rememberVectorPainter(image = Icons.Filled.PowerSettingsNew),
                        "login out",
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Surface(
                    shadowElevation = 10.dp,
                    shape = CircleShape
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(this@SettingActivity)
                            .data("https://s3-us-west-2.amazonaws.com/public.notion-static.com/19d871ea-9ec2-4964-8b30-fdbd15272d27/005zmwvEly8gkr67scc73j30ru0ru792.jpg")
                            .transformations(CircleCropTransformation())
                            .build(),
                        contentDescription = "workspace icon",
                        modifier = Modifier
                            .size(90.dp)
                    )
                }

                Text(
                    text = "Zhangke's WorkSpace",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 5.dp)
                ) {
                    Text(
                        text = "created by zhangke",
                        color = Color.Gray,
                        fontSize = 18.sp
                    )

                    AsyncImage(
                        model = ImageRequest.Builder(this@SettingActivity)
                            .data("https://s3-us-west-2.amazonaws.com/public.notion-static.com/19d871ea-9ec2-4964-8b30-fdbd15272d27/005zmwvEly8gkr67scc73j30ru0ru792.jpg")
                            .transformations(CircleCropTransformation())
                            .build(),
                        contentDescription = "workspace icon",
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .size(20.dp)
                    )
                }

                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 30.dp)
                ) {
                    Text(
                        text = getString(R.string.setting_page_manager),
                        color = Color.Gray,
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 10.dp)
                ) {
                    Text(
                        text = getString(R.string.setting_page_day_night),
                        color = Color.Gray,
                    )
                    Spacer(modifier = Modifier.weight(1F))
                    Text(
                        text = getString(R.string.setting_page_day_night_day),
                        color = Color.Gray,
                    )
                }


            }
        }
    }
}