package com.fatih.prayertime.util.composables


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import androidx.annotation.RawRes

/**
 * Lottie dosyasını göstermek için temel composable.
 * Uygulamadaki çeşitli ekranlarda kullanılabilir.
 *
 * @param lottieFile Lottie animasyon dosyasının assets klasöründeki adı
 * @param modifier Composable'ın görünümünü özelleştirmek için modifier
 * @param autoPlay Animasyonun otomatik başlayıp başlamayacağı
 * @param loop Animasyonun tekrar edip etmeyeceği
 * @param contentScale Animasyonun nasıl ölçekleneceği
 */
@Composable
fun LottieAnimationView(
    lottieFile: String,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true,
    loop: Boolean = true,
    contentScale: ContentScale = ContentScale.Fit
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset(lottieFile))

    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = autoPlay,
        iterations = if (loop) LottieConstants.IterateForever else 1,
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier,
        contentScale = contentScale
    )
}

/**
 * Kart içinde Lottie animasyon göstermek için composable.
 *
 * @param lottieFile Lottie animasyon dosyasının assets klasöründeki adı
 * @param modifier Composable'ın görünümünü özelleştirmek için modifier
 * @param autoPlay Animasyonun otomatik başlayıp başlamayacağı
 * @param loop Animasyonun tekrar edip etmeyeceği
 */
@Composable
fun LottieAnimationCard(
    lottieFile: String,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true,
    loop: Boolean = true
) {
    Card(
        modifier = modifier
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        LottieAnimationView(
            lottieFile = lottieFile,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            autoPlay = autoPlay,
            loop = loop
        )
    }
}

/**
 * Tek seferlik çalışan Lottie animasyonu için composable.
 * Animasyon tamamlandığında onFinish callback'i çağırılır.
 *
 * @param lottieFile Lottie animasyon dosyasının assets klasöründeki adı
 * @param modifier Composable'ın görünümünü özelleştirmek için modifier
 * @param onFinish Animasyon tamamlandığında çağrılacak callback
 */
@Composable
fun LottieAnimationOnce(
    lottieFile: String,
    modifier: Modifier = Modifier,
    onFinish: () -> Unit = {}
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset(lottieFile))
    var isFinished by remember { mutableFloatStateOf(0f) }

    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = true,
        iterations = 1,
    )

    LaunchedEffect(progress) {
        if (progress == 1f && isFinished != 1f) {
            isFinished = 1f
            onFinish()
        }
    }

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
}

/**
 * Tam ekran Lottie animasyonu için composable.
 *
 * @param lottieFile Lottie animasyon dosyasının assets klasöründeki adı
 * @param autoPlay Animasyonun otomatik başlayıp başlamayacağı
 * @param loop Animasyonun tekrar edip etmeyeceği
 */
@Composable
fun FullScreenLottieAnimation(
    lottieFile: String,
    autoPlay: Boolean = true,
    loop: Boolean = true
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimationView(
            lottieFile = lottieFile,
            modifier = Modifier.fillMaxSize(0.8f),
            autoPlay = autoPlay,
            loop = loop
        )
    }
}

/**
 * İkon boyutunda Lottie animasyonu için composable.
 *
 * @param lottieFile Lottie animasyon dosyasının assets klasöründeki adı
 * @param size İkonun boyutu
 * @param autoPlay Animasyonun otomatik başlayıp başlamayacağı
 * @param loop Animasyonun tekrar edip etmeyeceği
 */
@Composable
fun LottieAnimationIcon(
    lottieFile: String,
    size: Int = 48,
    autoPlay: Boolean = true,
    loop: Boolean = true
) {
    LottieAnimationView(
        lottieFile = lottieFile,
        modifier = Modifier.size(size.dp),
        autoPlay = autoPlay,
        loop = loop
    )
}

/**
 * Banner boyutunda yatay Lottie animasyonu için composable.
 *
 * @param lottieFile Lottie animasyon dosyasının assets klasöründeki adı
 * @param height Banner yüksekliği
 * @param autoPlay Animasyonun otomatik başlayıp başlamayacağı
 * @param loop Animasyonun tekrar edip etmeyeceği
 */
@Composable
fun LottieAnimationBanner(
    lottieFile: String,
    height: Int = 150,
    autoPlay: Boolean = true,
    loop: Boolean = true
) {
    LottieAnimationView(
        lottieFile = lottieFile,
        modifier = Modifier
            .fillMaxWidth()
            .height(height.dp),
        autoPlay = autoPlay,
        loop = loop,
        contentScale = ContentScale.FillWidth
    )
}

/**
 * Özel boyutlu Lottie animasyonu için composable.
 *
 * @param lottieFile Lottie animasyon dosyasının assets klasöründeki adı
 * @param width Animasyon genişliği
 * @param height Animasyon yüksekliği
 * @param autoPlay Animasyonun otomatik başlayıp başlamayacağı
 * @param loop Animasyonun tekrar edip etmeyeceği
 */
@Composable
fun LottieAnimationSized(
    lottieFile: String,
    width: Int,
    height: Int,
    autoPlay: Boolean = true,
    loop: Boolean = true
) {
    LottieAnimationView(
        lottieFile = lottieFile,
        modifier = Modifier
            .width(width.dp)
            .height(height.dp),
        autoPlay = autoPlay,
        loop = loop
    )
}

/**
 * Raw klasöründen bir Lottie animasyonu tek seferlik oynatmak için composable.
 * Animasyon tamamlandığında onFinish callback'i çağırılır.
 *
 * @param resId Lottie animasyon dosyasının raw klasöründeki resource ID'si
 * @param modifier Composable'ın görünümünü özelleştirmek için modifier
 * @param onFinish Animasyon tamamlandığında çağrılacak callback
 */
@Composable
fun LottieAnimationOnceRaw(
    @RawRes resId: Int,
    modifier: Modifier = Modifier,
    onFinish: () -> Unit = {}
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(resId))
    var isFinished by remember { mutableFloatStateOf(0f) }

    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = true,
        iterations = 1,
    )

    LaunchedEffect(progress) {
        if (progress == 1f && isFinished != 1f) {
            isFinished = 1f
            onFinish()
        }
    }

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
}