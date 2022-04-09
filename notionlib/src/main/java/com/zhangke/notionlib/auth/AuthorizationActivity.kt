package com.zhangke.notionlib.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhangke.framework.utils.toDp
import com.zhangke.notionlib.R
import kotlinx.coroutines.CoroutineScope

/**
 * 发起授权流程，并存储授权数据
 */
class AuthorizationActivity : ComponentActivity() {

    private val viewModel: AuthorizationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                AuthorizationScreen(vm = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }

    private fun handleIntent(intent: Intent) {
        viewModel.handleIntent(intent)
    }

    @Preview
    @Composable
    fun PreviewAuthorizationScreen() {
        AuthorizationScreen(AuthorizationViewModel())
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun AuthorizationScreen(vm: AuthorizationViewModel) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x99000000)),
        ) {

            val screenDp = resources.displayMetrics.heightPixels * 0.3F

            var toastShown by remember { mutableStateOf(false) }

//            var exit by remember { mutableStateOf(false) }
//            val yOffset by animateIntAsState(targetValue = if (exit) 500 else 0) {
//                finish()
//            }

            var show by remember { mutableStateOf(false) }
            AnimatedVisibility(
                modifier = Modifier
                    .padding(top = screenDp.toDp().dp)
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(30.dp, 30.dp, 0.dp, 0.dp)
                    ),
                visible = show,
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

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = 15.dp),
                    text = getString(R.string.notion_lib_auth_page_title),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                var cachedState by remember { mutableStateOf(0) }
                val authState by vm.authState.observeAsState()
                when (authState) {
                    2 -> {
                        if (!toastShown) {
                            Toast.makeText(
                                this@AuthorizationActivity,
                                R.string.notion_lib_auth_success,
                                Toast.LENGTH_SHORT
                            ).show()
                            toastShown = true
                            show = false
                        }
                    }
                    else -> {
                        cachedState = authState ?: 0
                    }
                }
                when (cachedState) {
                    0 -> WaitForUserClick(vm)
                    1 -> Loading(vm)
                    3 -> ShowAuthError(vm)
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
            Text(
                text = getString(R.string.notion_lib_auth_hint),
                modifier = Modifier.padding(horizontal = 60.dp, vertical = 0.dp),
                textAlign = TextAlign.Center,
            )
            Button(
                modifier = Modifier.offset(y = 10.dp),
                onClick = { vm.startAuth() }) {
                Text(text = getString(R.string.notion_lib_auth_perform_button))
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
            val hint = vm.authProccessInto.observeAsState()
            if (!hint.value.isNullOrEmpty()) {
                AnimatedContent(targetState = hint.value.orEmpty()) { targetHint ->
                    Text(
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
                painter = painterResource(id = R.drawable.notion_lib_error),
                contentDescription = null,
                tint = Color.DarkGray,
            )
            val hint = vm.authProccessInto.value
            if (!hint.isNullOrEmpty()) {
                Text(
                    modifier = Modifier.offset(y = 10.dp),
                    text = hint
                )
            }
        }
    }
}