package com.zhangke.architect.theme

import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.zhangke.architect.daynight.DayNightHelper
import com.zhangke.framework.R
import com.zhangke.framework.utils.appContext

private val textPrimaryColorDay =
    getColorFromRes(R.color.text_color_primary_day)
private val textSecondaryColorDay =
    getColorFromRes(R.color.text_color_secondary_day)
private val textPrimaryColorNight =
    getColorFromRes(R.color.text_color_primary_night)
private val textSecondaryColorNight =
    getColorFromRes(R.color.text_color_secondary_night)

val Colors.textPrimaryColor: Color
    get() {
        return if (isLight) {
            textPrimaryColorDay
        } else {
            textPrimaryColorNight
        }
    }

val Colors.textSecondaryColor: Color
    get() {
        return if (isLight) {
            textSecondaryColorDay
        } else {
            textSecondaryColorNight
        }
    }

private val lightColorTheme = lightColorScheme(
    primary = getColorFromRes(R.color.primary_day),
    primaryContainer = getColorFromRes(R.color.primary_variant_day),
    secondaryContainer = getColorFromRes(R.color.primary_variant_day),
    background = getColorFromRes(R.color.primary_variant_day),
    surface = getColorFromRes(R.color.primary_variant_day),
    onPrimary = getColorFromRes(R.color.on_primary_day),
    onSecondary = getColorFromRes(R.color.on_primary_secondary_day),
)

private val nightColorTheme = darkColorScheme(
    primary = getColorFromRes(R.color.primary_night),
    primaryContainer = getColorFromRes(R.color.primary_night),
    background = Color.Transparent,
    surface = getColorFromRes(R.color.surface_night),
)

private val lightColors = lightColors(
    primary = getColorFromRes(R.color.primary_day),
    primaryVariant = getColorFromRes(R.color.primary_variant_day),
    secondaryVariant = getColorFromRes(R.color.primary_variant_day),
    background = getColorFromRes(R.color.primary_variant_day),
    surface = getColorFromRes(R.color.primary_variant_day),
    onPrimary = getColorFromRes(R.color.on_primary_day),
    onSecondary = getColorFromRes(R.color.on_primary_secondary_day),
)

private val nightColors = darkColors(
    primary = getColorFromRes(R.color.primary_night),
    primaryVariant = getColorFromRes(R.color.primary_night),
    background = Color.Transparent,
    surface = getColorFromRes(R.color.surface_night),
)

private fun getColorFromRes(resId: Int): Color {
    return Color(appContext.resources.getColor(resId))
}

@Composable
fun AppMaterialTheme(content: @Composable () -> Unit) {
    val isNight = DayNightHelper.isNight()
    MaterialTheme(
        colorScheme = if (isNight) nightColorTheme else lightColorTheme
    ) {
        androidx.compose.material.MaterialTheme(
            colors = if (isNight) nightColors else lightColors
        ) {
            content()
        }
    }
}

@Composable
fun SecondaryText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = androidx.compose.material.MaterialTheme.colors.textSecondaryColor,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        onTextLayout = onTextLayout,
        style = style,
    )
}

@Composable
fun PrimaryText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = androidx.compose.material.MaterialTheme.colors.textPrimaryColor,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        onTextLayout = onTextLayout,
        style = style,
    )
}