package com.zhangke.notiontodo.setting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.zhangke.architect.activity.BaseActivity
import com.zhangke.architect.theme.AppMaterialTheme
import com.zhangke.architect.theme.PrimaryText
import com.zhangke.architect.theme.SecondaryText
import com.zhangke.notiontodo.R
import com.zhangke.notiontodo.code.OpenSourceActivity
import com.zhangke.notiontodo.pagemanager.PageManagerActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SettingActivity : BaseActivity() {

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
        val coroutineScope = rememberCoroutineScope()
        Scaffold {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .padding(top = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                val userInfo = vm.userInfo.observeAsState().value
                Surface(
                    shadowElevation = 10.dp,
                    shape = CircleShape
                ) {
                    if (userInfo == null) {
                        Image(
                            modifier = Modifier
                                .size(90.dp),
                            alpha = 0.5F,
                            painter = painterResource(R.drawable.ic_baseline_account_circle_24),
                            contentDescription = "workspace icon"
                        )
                    } else {
                        AsyncImage(
                            model = ImageRequest.Builder(this@SettingActivity)
                                .data(userInfo.workspaceIcon)
                                .transformations(CircleCropTransformation())
                                .build(),
                            contentDescription = "workspace icon",
                            modifier = Modifier
                                .size(90.dp)
                        )
                    }
                }

                if (userInfo != null) {
                    val ownerName = userInfo.owner.user.name
                    PrimaryText(
                        text = userInfo.workspaceName ?: "$ownerName's WorkSpace",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 5.dp)
                    ) {
                        SecondaryText(
                            text = "created by $ownerName",
                            color = Color.Gray,
                            fontSize = 18.sp
                        )

                        AsyncImage(
                            model = ImageRequest.Builder(this@SettingActivity)
                                .data(userInfo.workspaceIcon)
                                .transformations(CircleCropTransformation())
                                .build(),
                            contentDescription = "workspace icon",
                            modifier = Modifier
                                .padding(start = 5.dp)
                                .size(20.dp)
                        )
                    }
                } else {
                    PrimaryText(
                        text = getString(R.string.setting_not_login),
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(top = 10.dp)
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

                var dayNightModeExpanded by remember { mutableStateOf(false) }
                ConstraintLayout(
                    modifier = Modifier
                        .clickable {
                            dayNightModeExpanded = true
                        }
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 20.dp)
                ) {
                    val (iconId, titleId, subtitleId, dropDownMenu) = createRefs()
                    Icon(
                        modifier = Modifier
                            .size(20.dp, 20.dp)
                            .constrainAs(iconId) {
                                top.linkTo(titleId.top)
                                bottom.linkTo(subtitleId.bottom)
                                start.linkTo(parent.start)
                            },
                        painter = rememberVectorPainter(image = Icons.Filled.ArrowBack),
                        contentDescription = "icon1"
                    )

                    PrimaryText(
                        modifier = Modifier.constrainAs(titleId) {
                            top.linkTo(parent.top)
                            start.linkTo(iconId.end, margin = 20.dp)
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        text = getString(R.string.setting_page_day_night),
                    )

                    val currentDayNightMode = vm.currentDayNightMode.observeAsState().value
                    SecondaryText(
                        modifier = Modifier.constrainAs(subtitleId) {
                            top.linkTo(titleId.bottom, margin = 3.dp)
                            start.linkTo(titleId.start)
                        },
                        fontSize = 14.sp,
                        text = currentDayNightMode?.modeName.orEmpty(),
                        color = Color.Gray,
                    )
                    DropdownMenu(
                        modifier = Modifier.constrainAs(dropDownMenu) {
                            top.linkTo(subtitleId.bottom)
                            start.linkTo(subtitleId.start)
                        },
                        expanded = dayNightModeExpanded,
                        onDismissRequest = { dayNightModeExpanded = false }) {
                        vm.dayNightModeList.forEach {
                            DropdownMenuItem(
                                text = {
                                    Text(text = it.modeName)
                                },
                                onClick = {
                                    dayNightModeExpanded = false
                                    coroutineScope.launch {
                                        delay(200)
                                        vm.updateDayNight(it)
                                    }
                                }
                            )
                        }
                    }
                }

                CreateSettingLine(
                    modifier = Modifier.clickable {
                        vm.openAppMarket(this@SettingActivity)
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

                CreateSettingLine(
                    modifier = Modifier.clickable {
                        OpenSourceActivity.open(this@SettingActivity)
                    },
                    icon = Icons.Filled.ArrowBack,
                    title = getString(R.string.setting_page_open_source),
                    subtitle = getString(R.string.setting_page_open_source_desc)
                )
            }

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

                var showLogoutDialog by remember { mutableStateOf(false) }
                if (showLogoutDialog) {
                    AlertDialog(
                        onDismissRequest = { showLogoutDialog = false },
                        title = {
                            PrimaryText(
                                text = getString(R.string.setting_logout_dialog_title),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        },
                        dismissButton = {
                            Button(onClick = {
                                showLogoutDialog = false
                            }) {
                                PrimaryText(
                                    text = getString(R.string.cancel),
                                )
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                showLogoutDialog = false
                                vm.logout()
                            }) {
                                PrimaryText(
                                    text = getString(R.string.ok),
                                )
                            }
                        }
                    )
                }

                IconButton(
                    onClick = { showLogoutDialog = true }
                ) {
                    Icon(
                        painter = rememberVectorPainter(image = Icons.Filled.PowerSettingsNew),
                        "login out",
                    )
                }
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

            PrimaryText(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.constrainAs(titleId) {
                    top.linkTo(parent.top)
                    start.linkTo(iconId.end, margin = 20.dp)
                },
                text = title,
            )

            SecondaryText(
                fontSize = 14.sp,
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