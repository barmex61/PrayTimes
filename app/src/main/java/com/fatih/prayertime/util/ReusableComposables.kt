package com.fatih.prayertime.util

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LockScreenOrientation(orientation: Int){
    val context = LocalContext.current
    val activity = (context as? Activity)
    DisposableEffect (key1 = Unit){
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
        Canvas(modifier = Modifier.size(100.dp).graphicsLayer {
            rotationZ = angle
        }) {
            val radius = size.minDimension / 3
            val dotRadius = size.minDimension / 20
            val centerX = size.width / 2
            val centerY = size.height / 2

            for (i in 0 until 3) {
                val angleOffset = angle + (i * 120 )
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
fun ErrorView(message: String) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = "Error",
                tint = Color.Red,
                modifier = Modifier.size(64.dp).scale(scale)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = message, color = Color.Red, style = MaterialTheme.typography.bodyMedium)
        }
    }
}