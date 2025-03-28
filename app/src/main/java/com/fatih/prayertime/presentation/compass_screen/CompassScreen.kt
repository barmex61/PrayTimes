package com.fatih.prayertime.presentation.compass_screen

import android.content.pm.ActivityInfo
import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.fatih.prayertime.R
import com.fatih.prayertime.data.gyroscope.GyroscopeSensor
import com.fatih.prayertime.util.composables.LockScreenOrientation
import com.fatih.prayertime.util.composables.TitleView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@Composable
fun CompassScreen(modifier: Modifier, compassScreenViewModel: CompassScreenViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val gyroscopeSensor = remember { GyroscopeSensor(context) }
    val qiblaDirection by compassScreenViewModel.qiblaDirection.collectAsState()
    val scrollBehavior = rememberScrollState()
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    DisposableEffect(Unit) {
        onDispose {
            gyroscopeSensor.unregister()
        }
    }
    

    Column(
        modifier = modifier
            .verticalScroll(scrollBehavior),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CompassContent(
            gyroscopeSensor = gyroscopeSensor,
            qiblaDirection = qiblaDirection.toFloat(),
            modifier = modifier
        )
    }
    TitleView("Qibla Finder")
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CompassContent(
    gyroscopeSensor: GyroscopeSensor,
    qiblaDirection: Float,
    modifier: Modifier
) {
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

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 24.dp)
        ) {
            val icon = if (inRange) R.drawable.check_circle else R.drawable.rotate_arrow_icon
            
            Text(
                text = stringResource(R.string.kaaba_text),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
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
                Icon(
                    imageVector = ImageVector.vectorResource(it),
                    contentDescription = "Rotate Indicator",
                    modifier = Modifier
                        .size(50.dp)
                        .rotate(animatedRotationValue.value),
                    tint = animatedColor.value
                )
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.cross_icon),
                    contentDescription = "Cross",
                    modifier = Modifier.size(25.dp)
                )
                KaabaRepresentation((-25).dp, 0.dp, Color.Red)
                KaabaRepresentation(0.dp, 0.dp, Color.Green)
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.check_circle),
                    contentDescription = "Check",
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        CompassGyroscopeContent(
            gyroscopeSensor = gyroscopeSensor,
            qiblaDirection = qiblaDirection,
            inRange = inRange,
            onRangeChange = { inRange = it }
        )
    }
}

@Composable
fun CompassGyroscopeContent(
    gyroscopeSensor: GyroscopeSensor,
    qiblaDirection: Float,
    inRange: Boolean,
    onRangeChange: (Boolean) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .border(
                    width = 2.dp,
                    color = if (inRange) Color.Green else Color.Red,
                    shape = CircleShape
                )
        )
        
        val rotation = gyroscopeSensor.rotation.floatValue
        onRangeChange(rotation - 1f <= qiblaDirection && qiblaDirection <= rotation + 1f)
        var yOffset = 0.dp

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(top = 24.dp)
                .rotate(-gyroscopeSensor.rotation.floatValue)
                .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                .padding(16.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(R.drawable.compass_new),
                contentDescription = "Compass",
                modifier = Modifier.size(200.dp)
            )

            val compassRadius = 100.dp
            val circleRadius = 35.dp
            val kaabaRadius = 25.dp
            val distanceBetweenCenters = compassRadius + kaabaRadius + 40.dp + circleRadius / 3

            val angleInRadians = Math.toRadians(qiblaDirection.toDouble())
            val xOffset = (distanceBetweenCenters) * kotlin.math.sin(angleInRadians).toFloat()
            yOffset = distanceBetweenCenters * kotlin.math.cos(angleInRadians).toFloat()

            Image(
                painter = rememberAsyncImagePainter(R.drawable.kabe),
                contentDescription = "Kaaba",
                modifier = Modifier
                    .size(50.dp)
                    .offset(xOffset, -yOffset)
                    .rotate(gyroscopeSensor.rotation.floatValue)
            )
        }

        Spacer(modifier = Modifier.height(-yOffset  ))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.kaaba_angle) + " : $qiblaDirection",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.your_angle) + " : ${gyroscopeSensor.rotation.floatValue}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
fun KaabaRepresentation(offsetX: Dp, offsetY: Dp, color: Color) {
    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .border(
                    width = 2.dp,
                    color = color,
                    shape = CircleShape
                )
        )
        Image(
            painter = rememberAsyncImagePainter(R.drawable.kabe),
            contentDescription = "Kaaba",
            modifier = Modifier
                .size(50.dp)
                .offset(offsetX, offsetY)
        )
    }
}
