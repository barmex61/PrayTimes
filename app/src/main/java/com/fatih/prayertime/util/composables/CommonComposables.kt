package com.fatih.prayertime.util.composables

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    val activity = (context as? Activity)
    DisposableEffect(key1 = Unit) {
        activity?.requestedOrientation = orientation
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
}

@Composable
fun LoadingView() {
    val infiniteTransition = rememberInfiniteTransition()
    val circleColor = MaterialTheme.colorScheme.primary
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 240f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(
            modifier = Modifier
                .size(100.dp)
                .graphicsLayer {
                    rotationZ = angle
                }
        ) {
            val radius = size.minDimension / 3
            val dotRadius = size.minDimension / 20
            val centerX = size.width / 2
            val centerY = size.height / 2

            for (i in 0 until 3) {
                val angleOffset = angle + (i * 120)
                val radian = angleOffset * (PI / 180)
                val waveOffset = sin(radian * 3) * radius / 2
                val x = centerX + (radius + waveOffset) * cos(radian).toFloat()
                val y = centerY + (radius + waveOffset) * sin(radian).toFloat()

                drawCircle(
                    color = circleColor,
                    radius = dotRadius,
                    center = androidx.compose.ui.geometry.Offset(x.toFloat(), y.toFloat())
                )
            }
        }
    }
}

@Composable
fun ErrorView(message: String, onRetry: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val rotate by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(
            onClick = onRetry,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Spacer(modifier = Modifier.size(16.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(16.dp)
                        .background(color = Color.Transparent)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Retry",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(64.dp)
                            .graphicsLayer {
                                rotationZ = rotate
                            }
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    Text(
                        text = "Click to refresh",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                    )
                }

                Text(
                    textAlign = TextAlign.Center,
                    text = message,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun TitleView(title: String) {
    val visible = remember { mutableStateOf(false) }

    AnimatedVisibility(
        modifier = Modifier
            .fillMaxWidth()
            .padding(25.dp),
        visible = visible.value,
        enter = fadeIn(animationSpec = tween(1000)) +
                scaleIn(initialScale = 0.5f, animationSpec = tween(1000)) +
                expandIn(expandFrom = Alignment.BottomEnd, animationSpec = tween(800)),
        exit = fadeOut(animationSpec = tween(1000)) +
                scaleOut(targetScale = 0.5f, animationSpec = tween(1000)) +
                shrinkOut(shrinkTowards = Alignment.BottomCenter, animationSpec = tween(800))
    ) {
        Box(
            modifier = Modifier.padding(50.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(20.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    LaunchedEffect(Unit) {
        visible.value = true
        delay(1500)
        visible.value = false
    }
} 