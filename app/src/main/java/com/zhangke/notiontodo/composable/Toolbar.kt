package com.zhangke.notiontodo.composable

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.sp
import com.zhangke.architect.theme.PrimaryText

@Composable
fun Toolbar(
    title: String,
    navigationBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val navigationIcon: (@Composable (() -> Unit))? = if (navigationBackClick != null) {
        @Composable
        {
            IconButton(onClick = { navigationBackClick() }) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Filled.ArrowBack),
                    "back"
                )
            }
        }
    } else {
        null
    }
    TopAppBar(
        navigationIcon = navigationIcon,
        actions = actions,
        backgroundColor = MaterialTheme.colorScheme.background,
        title = {
            PrimaryText(
                text = title,
                fontSize = 18.sp,
            )
        },
    )
}