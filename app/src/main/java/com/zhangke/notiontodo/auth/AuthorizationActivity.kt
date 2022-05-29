package com.zhangke.notiontodo.auth

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.zhangke.architect.activity.BaseActivity
import com.zhangke.architect.theme.AppMaterialTheme
import com.zhangke.architect.theme.PrimaryText
import com.zhangke.architect.theme.SecondaryText
import com.zhangke.architect.theme.textPrimaryColor
import com.zhangke.framework.utils.appContext
import com.zhangke.framework.utils.toDp
import com.zhangke.framework.utils.toast
import com.zhangke.notiontodo.R
import com.zhangke.notiontodo.composable.AppColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 发起授权流程，并存储授权数据
 */
class AuthorizationActivity : BaseActivity() {

    companion object {

        fun open() {
            val intent = Intent(appContext, AuthorizationActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            appContext.startActivity(intent)
        }
    }

    private val viewModel: AuthorizationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppMaterialTheme {
                AuthorizationScreen(vm = viewModel)
            }
        }
        intent?.let { handleIntent(it) }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }

    private fun handleIntent(intent: Intent) {
        viewModel.handleIntent(intent)
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun AuthorizationScreen(vm: AuthorizationViewModel) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColor.translucentBackground),
        ) {
            val screenDp = resources.displayMetrics.heightPixels * 0.3F
            val hide by vm.authSuccess.observeAsState(false)
            if (hide) {
                SideEffect {
                    toast(R.string.auth_success)
                    lifecycleScope.launch {
                        delay(200)
                        this@AuthorizationActivity.finish()
                    }
                }
            }
            AnimatedVisibility(
                modifier = Modifier
                    .padding(top = screenDp.toDp().dp)
                    .background(
                        MaterialTheme.colors.surface,
                        shape = RoundedCornerShape(30.dp, 30.dp, 0.dp, 0.dp)
                    ),
                visible = !hide,
                enter = slideInVertically(
                    animationSpec = tween(durationMillis = 150, easing = LinearOutSlowInEasing),
                    initialOffsetY = { fullHeight ->
                        fullHeight
                    }),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 250, easing = FastOutLinearInEasing),
                ),
            ) {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = 15.dp)
                ) {
                    val (title, optionIcon) = createRefs()
                    PrimaryText(
                        modifier = Modifier
                            .constrainAs(title) {
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                            },
                        text = getString(R.string.auth_page_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    var showDialog by remember { mutableStateOf(false) }
                    if (showDialog) {
                        Dialog(
                            onDismissRequest = { showDialog = false }
                        ) {
                            Card(
                                elevation = 15.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = MaterialTheme.colors.background
                                    )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(15.dp)
                                ) {

                                    PrimaryText(
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally),
                                        text = getString(R.string.auth_manually_input_code),
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 18.sp
                                    )
                                    SecondaryText(
                                        modifier = Modifier
                                            .padding(top = 15.dp)
                                            .align(Alignment.CenterHorizontally),
                                        text = getString(R.string.auth_manually_input_code_desc),
                                        fontSize = 14.sp
                                    )
                                    var inputtedText by remember { mutableStateOf("") }
                                    val inputError = vm.inputError.observeAsState(false)
                                    TextField(
                                        value = inputtedText,
                                        shape = RoundedCornerShape(6.dp),
                                        onValueChange = {
                                            vm.inputError.value = false
                                            inputtedText = it
                                        },
                                        placeholder = {
                                            SecondaryText(
                                                text = getString(R.string.auth_manually_input_hint),
                                            )
                                        },
                                        isError = inputError.value,
                                        colors = TextFieldDefaults.textFieldColors(
                                            backgroundColor = Color(0x99CCCCCC),
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(100.dp)
                                            .padding(top = 3.dp),
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.End,
                                        modifier = Modifier
                                            .padding(top = 15.dp)
                                            .align(Alignment.End)
                                    ) {
                                        Button(
                                            modifier = Modifier.padding(end = 15.dp),
                                            onClick = {
                                                showDialog = false
                                            }) {
                                            PrimaryText(
                                                text = getString(R.string.cancel),
                                                color = Color.White
                                            )
                                        }

                                        Button(onClick = {
                                            showDialog = !vm.onInputtedText(inputtedText)
                                        }) {
                                            PrimaryText(
                                                text = getString(R.string.ok),
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Icon(
                        modifier = Modifier
                            .size(25.dp, 25.dp)
                            .clickable {
                                showDialog = true
                            }
                            .constrainAs(optionIcon) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                end.linkTo(parent.end, margin = 30.dp)
                            },
                        painter = rememberVectorPainter(image = Icons.Filled.BugReport),
                        tint = MaterialTheme.colors.textPrimaryColor,
                        contentDescription = "Input manually"
                    )
                }

                val authState by vm.authState.observeAsState()
                when (authState) {
                    0 -> WaitForUserClick(vm)
                    1 -> Loading(vm)
                    2 -> ShowAuthError(vm)
                }
            }
        }
    }

    @Composable
    fun WaitForUserClick(vm: AuthorizationViewModel) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PrimaryText(
                text = getString(R.string.auth_hint),
                modifier = Modifier.padding(horizontal = 60.dp, vertical = 0.dp),
                textAlign = TextAlign.Center,
            )
            Button(
                modifier = Modifier.offset(y = 10.dp),
                onClick = { vm.startAuth() }) {
                PrimaryText(
                    color = Color.White,
                    text = getString(R.string.auth_perform_button)
                )
            }
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun Loading(vm: AuthorizationViewModel) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(130.dp),
                strokeWidth = 6.dp
            )
            val hint = vm.authProcessInfo.observeAsState()
            if (!hint.value.isNullOrEmpty()) {
                AnimatedContent(targetState = hint.value.orEmpty()) { targetHint ->
                    PrimaryText(
                        modifier = Modifier.offset(y = 10.dp),
                        text = targetHint
                    )
                }
            }
        }
    }

    @Composable
    fun ShowAuthError(vm: AuthorizationViewModel) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                modifier = Modifier.size(80.dp),
                painter = rememberVectorPainter(Icons.Filled.Error),
                contentDescription = null,
                tint = Color.DarkGray,
            )
            val hint = vm.authProcessInfo.value
            if (!hint.isNullOrEmpty()) {
                PrimaryText(
                    modifier = Modifier.offset(y = 10.dp),
                    text = hint
                )
            }
        }
    }
}