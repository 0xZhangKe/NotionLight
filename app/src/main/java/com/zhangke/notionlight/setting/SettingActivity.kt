package com.zhangke.notionlight.setting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.zhangke.architect.activity.BaseActivity
import com.zhangke.architect.theme.*
import com.zhangke.notionlight.R
import com.zhangke.notionlight.code.OpenSourceActivity
import com.zhangke.notionlight.draft.DraftBoxActivity
import com.zhangke.notionlight.draft.DraftBoxManager
import com.zhangke.notionlight.pagemanager.PageManagerActivity
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
                                .placeholder(R.drawable.ic_baseline_account_circle_24)
                                .error(R.drawable.ic_baseline_account_circle_24)
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
                    iconResId = R.drawable.ic_page_manager,
                    iconPadding = 3.dp,
                    title = getString(R.string.setting_page_manager),
                    subtitle = getString(R.string.setting_page_manager_subtitle),
                )

                CreateSettingLine(
                    modifier = Modifier.clickable {
                        DraftBoxActivity.open(this@SettingActivity)
                    },
                    iconResId = R.drawable.ic_baseline_drafts_24,
                    iconPadding = 3.dp,
                    title = getString(R.string.setting_draft_box),
                    subtitle = getString(R.string.setting_draft_box_subtitle),
                )

                var dayNightModeMenuExpanded by remember { mutableStateOf(false) }
                val currentDayNightMode = vm.currentDayNightMode.observeAsState().value
                val currentDayNightModeName = if (currentDayNightMode != null) {
                    vm.getDayNightVm(currentDayNightMode).name
                } else {
                    ""
                }
                CreateSettingLine(
                    modifier = Modifier.clickable {
                        dayNightModeMenuExpanded = true
                    },
                    iconResId = R.drawable.ic_night_mode,
                    title = getString(R.string.setting_page_day_night),
                    subtitle = currentDayNightModeName,
                    onDismissRequest = { dayNightModeMenuExpanded = false },
                    menuExpanded = dayNightModeMenuExpanded
                ) {
                    vm.getDayNightModeList().forEach {
                        DropdownMenuItem(
                            text = {
                                Text(text = it.name)
                            },
                            onClick = {
                                dayNightModeMenuExpanded = false
                                coroutineScope.launch {
                                    delay(200)
                                    vm.updateDayNight(it)
                                }
                            }
                        )
                    }
                }

                var languageMenuExpanded by remember { mutableStateOf(false) }
                val currentLanguage = vm.getCurrentLanguage()
                CreateSettingLine(
                    modifier = Modifier.clickable {
                        languageMenuExpanded = true
                    },
                    iconResId = R.drawable.ic_language,
                    iconPadding = 4.dp,
                    title = getString(R.string.setting_language),
                    subtitle = currentLanguage.name,
                    onDismissRequest = { languageMenuExpanded = false },
                    menuExpanded = languageMenuExpanded
                ) {
                    vm.getSupportedLanguage().forEach {
                        DropdownMenuItem(
                            text = {
                                Text(text = it.name)
                            },
                            onClick = {
                                languageMenuExpanded = false
                                vm.setLanguage(this@SettingActivity, it)
                            }
                        )
                    }
                }

                CreateSettingLine(
                    modifier = Modifier.clickable {
                        HelpActivity.open(this@SettingActivity)
                    },
                    iconResId = R.drawable.ic_help,
                    iconPadding = 4.dp,
                    title = getString(R.string.setting_page_help),
                    subtitle = getString(R.string.setting_page_help_desc)
                )

                CreateSettingLine(
                    modifier = Modifier.clickable {
                        OpenSourceActivity.open(this@SettingActivity)
                    },
                    icon = Icons.Filled.Code,
                    title = getString(R.string.setting_page_open_source),
                    subtitle = getString(R.string.setting_page_open_source_desc)
                )

                val showFeedbackDialog = remember { mutableStateOf(false) }
                if (showFeedbackDialog.value) {
                    FeedbackDialog(vm) {
                        showFeedbackDialog.value = false
                    }
                }
                CreateSettingLine(
                    modifier = Modifier.clickable {
                        showFeedbackDialog.value = true
                    },
                    icon = Icons.Outlined.Feedback,
                    iconPadding = 3.dp,
                    title = getString(R.string.setting_feedback),
                    subtitle = getString(R.string.setting_feedback_desc)
                )

                CreateSettingLine(
                    modifier = Modifier.clickable {
                        vm.openAppMarket(this@SettingActivity)
                    },
                    iconResId = R.drawable.ic_ratting,
                    iconPadding = 5.dp,
                    title = getString(R.string.setting_page_appraise),
                    subtitle = getString(R.string.setting_page_appraise_desc)
                )

                CreateSettingLine(
                    modifier = Modifier.clickable {

                    },
                    iconResId = R.mipmap.logo,
                    title = getString(R.string.setting_page_about_title),
                    tintColor = null,
                    subtitle = vm.getAppVersionDesc()
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
                                    color = Color.White
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
                                    color = Color.White
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
        icon: ImageVector? = null,
        iconResId: Int? = null,
        iconPadding: Dp = 0.dp,
        tintColor: Color? = androidx.compose.material.MaterialTheme.colors.textPrimaryColor,
        title: String,
        subtitle: String,
        menuExpanded: Boolean = false,
        onDismissRequest: (() -> Unit)? = null,
        dropDownItems: (@Composable () -> Unit)? = null
    ) {
        ConstraintLayout(
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 20.dp)
        ) {
            val (iconId, titleId, subtitleId, dropDownMenu) = createRefs()
            if (icon != null) {
                Icon(
                    modifier = Modifier
                        .size(30.dp, 30.dp)
                        .padding(iconPadding)
                        .constrainAs(iconId) {
                            top.linkTo(titleId.top)
                            bottom.linkTo(subtitleId.bottom)
                            start.linkTo(parent.start)
                        },
                    tint = tintColor ?: LocalContentColor.current,
                    painter = rememberVectorPainter(image = icon),
                    contentDescription = null
                )
            } else {
                Image(
                    modifier = Modifier
                        .size(30.dp, 30.dp)
                        .padding(iconPadding)
                        .constrainAs(iconId) {
                            top.linkTo(titleId.top)
                            bottom.linkTo(subtitleId.bottom)
                            start.linkTo(parent.start)
                        },
                    colorFilter = if (tintColor == null) null else ColorFilter.tint(tintColor),
                    painter = painterResource(id = iconResId!!),
                    contentDescription = null
                )
            }

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
            if (dropDownItems != null) {
                DropdownMenu(
                    modifier = Modifier.constrainAs(dropDownMenu) {
                        top.linkTo(subtitleId.bottom)
                        start.linkTo(subtitleId.start)
                    },
                    expanded = menuExpanded,
                    onDismissRequest = onDismissRequest!!
                ) {
                    dropDownItems()
                }
            }
        }
    }

    @Composable
    fun FeedbackDialog(vm: SettingViewModel, onDismissRequest: () -> Unit) {
        Dialog(onDismissRequest = onDismissRequest) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(8.dp)
                    )
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            vm.feedbackByAppStore(this@SettingActivity)
                            onDismissRequest()
                        }
                        .padding(25.dp, 10.dp, 25.dp, 5.dp)
                ) {
                    PrimaryText(
                        modifier = Modifier.padding(13.dp, 13.dp, 13.dp, 13.dp),
                        text = getString(R.string.setting_feedback_dialog_app_store)
                    )
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            vm.feedbackByEmail(this@SettingActivity)
                            onDismissRequest()
                        }
                        .padding(25.dp, 5.dp, 25.dp, 5.dp)
                ) {
                    PrimaryText(
                        modifier = Modifier.padding(13.dp, 13.dp, 13.dp, 13.dp),
                        text = getString(R.string.setting_feedback_dialog_email)
                    )
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            vm.feedbackByGithub(this@SettingActivity)
                            onDismissRequest()
                        }
                        .padding(25.dp, 5.dp, 25.dp, 10.dp)
                ) {
                    PrimaryText(
                        modifier = Modifier.padding(13.dp, 13.dp, 13.dp, 13.dp),
                        text = getString(R.string.setting_feedback_dialog_github)
                    )
                }
            }
        }
    }
}