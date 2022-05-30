package com.zhangke.notionlight.code

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.zhangke.architect.activity.BaseActivity
import com.zhangke.architect.theme.AppMaterialTheme
import com.zhangke.architect.theme.PrimaryText
import com.zhangke.architect.theme.SecondaryText
import com.zhangke.notionlight.composable.Toolbar
import com.zhangke.notionlight.R

class OpenSourceActivity : BaseActivity() {

    companion object {

        fun open(activity: Activity) {
            Intent(activity, OpenSourceActivity::class.java).let {
                activity.startActivity(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm: OpenSourceViewModel by viewModels()
        setContent {
            AppMaterialTheme {
                Page(vm)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Page(viewModel: OpenSourceViewModel) {
        val listState: LazyListState = rememberLazyListState()
        Scaffold(
            topBar = {
                Toolbar(
                    title = getString(R.string.open_source_page_title),
                    navigationBackClick = { finish() }
                )
            }) {
            val thisProjectInfo = viewModel.thisOpenSourceInfo.observeAsState().value
            val otherList = viewModel.openSourceInfoList.observeAsState().value
            val list = mutableListOf<Any>()
            thisProjectInfo?.let { list += it }
            otherList?.let { list.addAll(it) }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                contentPadding = PaddingValues(top = 15.dp, bottom = 50.dp)
            ) {
                items(
                    list.size,
                    contentType = {
                        list[it]::class.java
                    }
                ) { index ->
                    val item = list[index]
                    if (item is OpenSourceViewModel.ThisProjectOpenSourceInfo) {
                        Surface(
                            modifier = Modifier
                                .padding(top = 12.dp, bottom = 12.dp)
                                .clickable {
                                    viewModel.openBrowser(this@OpenSourceActivity, item.url)
                                }
                                .fillMaxWidth(),
                            elevation = 5.dp
                        ) {
                            ConstraintLayout(
                                modifier = Modifier
                                    .padding(start = 20.dp, top = 8.dp, bottom = 8.dp, end = 5.dp)
                                    .fillMaxWidth()
                            ) {
                                val (logo, title, subtitle1, subtitle2) = createRefs()
                                Image(
                                    painter = painterResource(id = R.mipmap.logo),
                                    contentDescription = "logo",
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .size(50.dp)
                                        .constrainAs(logo) {
                                            start.linkTo(parent.start)
                                            top.linkTo(parent.top)
                                            bottom.linkTo(parent.bottom)
                                        }
                                )

                                PrimaryText(
                                    text = "${item.name} - ${item.author}",
                                    fontSize = 16.sp,
                                    modifier = Modifier.constrainAs(title) {
                                        top.linkTo(parent.top)
                                        start.linkTo(logo.end, margin = 15.dp)
                                    }
                                )

                                SecondaryText(
                                    text = item.license,
                                    fontSize = 14.sp,
                                    modifier = Modifier.constrainAs(subtitle1) {
                                        top.linkTo(title.bottom, margin = 2.dp)
                                        start.linkTo(title.start)
                                    }
                                )

                                SecondaryText(
                                    text = item.url,
                                    fontSize = 14.sp,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .constrainAs(subtitle2) {
                                            top.linkTo(subtitle1.bottom, margin = 2.dp)
                                            start.linkTo(title.start)
                                        }
                                )
                            }
                        }
                    } else {
                        item as OpenSourceViewModel.OpenSourceInfo
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.openBrowser(this@OpenSourceActivity, item.url)
                                }
                                .padding(bottom = 12.dp, top = 12.dp),
                            elevation = 5.dp,
                        ) {
                            Column(
                                modifier = Modifier.padding(
                                    start = 20.dp,
                                    top = 8.dp,
                                    bottom = 8.dp,
                                    end = 5.dp
                                )
                            ) {
                                PrimaryText(
                                    text = "${item.name} - ${item.author}",
                                    fontSize = 16.sp,
                                )

                                SecondaryText(
                                    text = item.license,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )

                                SecondaryText(
                                    text = item.url,
                                    fontSize = 14.sp,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}