package com.jr.customlist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.jr.customlist.ui.theme.CustomListTheme
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.hypot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CustomListTheme {
                CustomListExample()
            }
        }
    }
}

@Composable
fun CustomListExample() {
    val colors = remember { Data.colors }
    var previousBackgroundColor by remember { mutableStateOf(colors.first()) }
    var currentBackgroundColor by remember { mutableStateOf(colors.first()) }
    val (width, height) = with(LocalConfiguration.current) {
        with(LocalDensity.current) { screenWidthDp.dp.toPx() to screenHeightDp.dp.toPx() }
    }
    val maxRadiusPx = hypot(width, height)
    var radius by remember { mutableFloatStateOf(0f) }
    var clickedOffset: Offset? by remember { mutableStateOf(null) }
    val animatedRadius = remember { Animatable(0f) }
    LaunchedEffect(key1 = true) {
        snapshotFlow { currentBackgroundColor }.collectLatest {
            animatedRadius.animateTo(
                maxRadiusPx,
                animationSpec = tween(500, easing = LinearEasing)
            ) {
                radius = value

            }
            animatedRadius.animateTo(0f, animationSpec = tween(durationMillis = 50)) {
                previousBackgroundColor = currentBackgroundColor
            }
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .drawWithCache {
            onDrawBehind {
                drawRect(
                    Brush.linearGradient(
                        listOf(
                            previousBackgroundColor.startColor, previousBackgroundColor.endColor
                        )
                    )
                )
                drawCircle(
                    brush = Brush.linearGradient(
                        listOf(
                            currentBackgroundColor.startColor, currentBackgroundColor.endColor
                        )
                    ),
                    radius = radius,
                    center = clickedOffset ?: Offset(size.width / 2, size.height / 2),
                )
            }
        }) {
        CustomList(visibleItems = 3,
            modifier = Modifier.fillMaxWidth(),
            onItemClick = { selectedIndex, offset ->
                clickedOffset = offset
                currentBackgroundColor = colors[selectedIndex]
            }) {
            colors.forEachIndexed { index, color ->
                ColorComponent(color = color)
            }
        }
    }
}