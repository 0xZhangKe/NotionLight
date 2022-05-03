package com.zhangke.notiontodo.composable

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.zhangke.framework.utils.appContext
import com.zhangke.notiontodo.R
import com.zhangke.notiontodo.daynight.DayNightHelper

private val lightColorTheme = lightColorScheme(
//    primary = Color(appContext.resources.getColor(R.color.primary_day)),
    primaryContainer = Color(appContext.resources.getColor(R.color.primary_variant_day)),
//    secondary = Color(appContext.resources.getColor(R.color.primary_day)),
    secondaryContainer = Color(appContext.resources.getColor(R.color.primary_variant_day)),
    background = Color(appContext.resources.getColor(R.color.primary_variant_day)),
    surface = Color(appContext.resources.getColor(R.color.primary_variant_day)),
    onPrimary = Color(appContext.resources.getColor(R.color.on_primary_day)),
    onSecondary = Color(appContext.resources.getColor(R.color.on_primary_secondary_day)),
)

private val nightColorTheme = darkColorScheme(
//    primary = Color(appContext.resources.getColor(R.color.primary_night)),
    primaryContainer = Color(appContext.resources.getColor(R.color.primary_variant_night)),
//    secondary = Color(appContext.resources.getColor(R.color.primary_night)),
    secondaryContainer = Color(appContext.resources.getColor(R.color.primary_variant_night)),
    background = Color(appContext.resources.getColor(R.color.primary_variant_night)),
    surface = Color(appContext.resources.getColor(R.color.primary_variant_night)),
    onPrimary = Color(appContext.resources.getColor(R.color.on_primary_night)),
    onSecondary = Color(appContext.resources.getColor(R.color.on_primary_secondary_night)),
)

private val lightColors = lightColors(
//    primary = Color(appContext.resources.getColor(R.color.primary_day)),
    primaryVariant = Color(appContext.resources.getColor(R.color.primary_variant_day)),
//    secondary = Color(appContext.resources.getColor(R.color.primary_day)),
    secondaryVariant = Color(appContext.resources.getColor(R.color.primary_variant_day)),
    background = Color(appContext.resources.getColor(R.color.primary_variant_day)),
    surface = Color(appContext.resources.getColor(R.color.primary_variant_day)),
    onPrimary = Color(appContext.resources.getColor(R.color.on_primary_day)),
    onSecondary = Color(appContext.resources.getColor(R.color.on_primary_secondary_day)),
)

private val nightColors = darkColors(
//    primary = Color(appContext.resources.getColor(R.color.primary_night)),
    primaryVariant = Color(appContext.resources.getColor(R.color.primary_variant_night)),
//    secondary = Color(appContext.resources.getColor(R.color.primary_night)),
    secondaryVariant = Color(appContext.resources.getColor(R.color.primary_variant_night)),
    background = Color(appContext.resources.getColor(R.color.primary_variant_night)),
    surface = Color(appContext.resources.getColor(R.color.primary_variant_night)),
    onPrimary = Color(appContext.resources.getColor(R.color.on_primary_night)),
    onSecondary = Color(appContext.resources.getColor(R.color.on_primary_secondary_night)),
)

@Composable
fun AppMaterialTheme(content: @Composable () -> Unit) {
    val isNight = DayNightHelper.isNight()
    MaterialTheme(
        colorScheme = if (isNight) nightColorTheme else lightColorScheme()
    ) {
        androidx.compose.material.MaterialTheme(
            colors = if (isNight) nightColors else lightColors
        ) {
            content()
        }
    }
}