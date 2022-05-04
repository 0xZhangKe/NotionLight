package com.zhangke.notiontodo.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.architect.theme.PrimaryText
import com.zhangke.framework.utils.appContext
import com.zhangke.notiontodo.R

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PageLoading(hint: String? = appContext.getString(R.string.loading)) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(130.dp),
            strokeWidth = 6.dp
        )
        if (!hint.isNullOrEmpty()) {
            AnimatedContent(targetState = hint.orEmpty()) { targetHint ->
                PrimaryText(
                    modifier = Modifier.offset(y = 10.dp),
                    text = targetHint
                )
            }
        }
    }
}