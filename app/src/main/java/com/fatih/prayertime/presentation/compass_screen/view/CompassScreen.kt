package com.fatih.prayertime.presentation.compass_screen.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fatih.prayertime.R
import com.fatih.prayertime.data.gyroscope.GyroscopeSensor
import com.fatih.prayertime.presentation.compass_screen.viewmodel.CompassScreenViewModel


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CompassScreen() {

    val context = LocalContext.current
    val gyroscopeSensor = remember { GyroscopeSensor(context) }
    val compassScreenViewModel : CompassScreenViewModel = hiltViewModel()
    val qiblaDirection by compassScreenViewModel.qiblaDirection.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            gyroscopeSensor.unregister()
        }
    }
    Column (modifier = Modifier.fillMaxSize(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
        var inRange by remember { mutableStateOf(false) }
        val animatedColor = animateColorAsState(
            targetValue = if (inRange) Color.Green else Color.Red,
            animationSpec = tween(1000),
            label = ""
        )
        val infiniteTransition = rememberInfiniteTransition(label = "")
        val animatedRotationValue = infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = if (inRange) 0f else 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000),
                repeatMode = RepeatMode.Restart
            ),
            label = "",
        )
        val icon = if (inRange) R.drawable.check_circle else R.drawable.rotate_arrow_icon

        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
            Text(
                text = "Kabe resmi çember içerisine gelmeli",
                color = animatedColor.value
            )
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = "Telefonunuzu kabeyi gösterecek şekilde çevirin",
                color = animatedColor.value
            )
            Spacer(modifier = Modifier.size(20.dp))
            AnimatedContent(
                targetState = icon,
                label = "",
                transitionSpec = {
                    fadeIn(animationSpec = tween(1000)) with
                    fadeOut(animationSpec = tween(1000))
                }
            ) {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = "Rotate Indicator",
                    modifier = Modifier
                        .size(50.dp)
                        .rotate(animatedRotationValue.value),
                    colorFilter = ColorFilter.tint(animatedColor.value)
                )
            }

            Spacer(modifier = Modifier.size(30.dp))
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier.size(70.dp).border(
                    width = 2.dp,
                    color = animatedColor.value,
                    shape = CircleShape
                )
            )
            val totalDegree = qiblaDirection + gyroscopeSensor.rotation.floatValue
            inRange = totalDegree in -1.5f..1.5f ||
                    totalDegree in 358.5f..361.5f

            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(top = 30.dp).rotate(-gyroscopeSensor.rotation.floatValue)){

                Image(
                    painter = painterResource(id = R.drawable.compass_new),
                    contentDescription = "Compass",
                    modifier = Modifier.size(250.dp)
                )
                val compassRadius = with(LocalDensity.current) { 125.dp.toPx() } // Compass radius in px
                val circleRadius = with(LocalDensity.current) { 35.dp.toPx() }
                val kaabaRadius = with(LocalDensity.current) { 25.dp.toPx() } // Circle radius in px (70.dp / 2)
                val distanceBetweenCenters = compassRadius - circleRadius - kaabaRadius// Distance between the centers

                val angleInRadians = Math.toRadians(qiblaDirection)
                val xOffset = compassRadius * kotlin.math.sin(angleInRadians).toFloat()
                val yOffset = (compassRadius * kotlin.math.cos(angleInRadians).toFloat()).coerceIn(-distanceBetweenCenters,distanceBetweenCenters)
                Image(
                    painter = painterResource(id = R.drawable.kabe),
                    contentDescription = "Kaaba",
                    modifier = Modifier
                        .size(50.dp)
                        .offset(x = xOffset.dp, y = -yOffset.dp)
                )
            }
        }
    }

}
