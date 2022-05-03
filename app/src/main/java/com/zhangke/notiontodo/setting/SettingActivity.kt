package com.zhangke.notiontodo.setting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.zhangke.notiontodo.R
import com.zhangke.notiontodo.composable.AppMaterialTheme
import com.zhangke.notiontodo.pagemanager.PageManagerActivity

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
            AppMaterialTheme {
                Page(vm)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Page(vm: SettingViewModel) {
        Scaffold {
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

                CreateSettingLine(
                    modifier = Modifier.clickable {
                        PageManagerActivity.open(this@SettingActivity)
                    },
                    icon = Icons.Filled.ArrowBack,
                    title = getString(R.string.setting_page_manager),
                    subtitle = getString(R.string.setting_page_manager),
                )

                CreateSettingLine(
                    modifier = Modifier.clickable {

                    },
                    icon = Icons.Filled.ArrowBack,
                    title = getString(R.string.setting_page_day_night),
                    subtitle = getString(R.string.setting_page_day_night_day)
                )

                CreateSettingLine(
                    modifier = Modifier.clickable {

                    },
                    icon = Icons.Filled.ArrowBack,
                    title = getString(R.string.setting_page_appraise),
                    subtitle = getString(R.string.setting_page_appraise_desc)
                )

                CreateSettingLine(
                    modifier = Modifier.clickable {

                    },
                    icon = Icons.Filled.ArrowBack,
                    title = getString(R.string.setting_page_about_title),
                    subtitle = vm.getAppVersionDesc()
                )

                CreateSettingLine(
                    modifier = Modifier.clickable {

                    },
                    icon = Icons.Filled.ArrowBack,
                    title = getString(R.string.setting_page_help),
                    subtitle = getString(R.string.setting_page_help_desc)
                )
            }
        }
    }

    @Composable
    fun CreateSettingLine(
        modifier: Modifier,
        icon: ImageVector,
        title: String,
        subtitle: String,
    ) {
        ConstraintLayout(
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 20.dp)
        ) {
            val (iconId, titleId, subtitleId) = createRefs()
            Icon(
                modifier = Modifier
                    .size(20.dp, 20.dp)
                    .constrainAs(iconId) {
                        top.linkTo(titleId.top)
                        bottom.linkTo(subtitleId.bottom)
                        start.linkTo(parent.start)
                    },
                painter = rememberVectorPainter(image = icon),
                contentDescription = "icon1"
            )

            Text(
                modifier = Modifier.constrainAs(titleId) {
                    top.linkTo(parent.top)
                    start.linkTo(iconId.end, margin = 20.dp)
                },
                text = title,
                color = Color.Black,
            )

            Text(
                modifier = Modifier.constrainAs(subtitleId) {
                    top.linkTo(titleId.bottom, margin = 3.dp)
                    start.linkTo(titleId.start)
                },
                text = subtitle,
                color = Color.Gray,
            )
        }
    }
}