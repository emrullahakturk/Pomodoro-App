package com.yargisoft.pomodoroapp.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yargisoft.pomodoroapp.ui.home.PomodoroState

@Composable
fun TimerDisplay(
    timeMillis: Long,
    pomodoroState: PomodoroState,
    totalTimeMillis: Long, // Toplam süre (örneğin 25 dakika)
    modifier: Modifier = Modifier
) {
    val progress = 1f - (timeMillis.toFloat() / totalTimeMillis.toFloat()) // 0.0 (başlangıç) -> 1.0 (bitiş)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 900), label = "progressAnimation"
    )

    val progressBarColor = when (pomodoroState) {
        PomodoroState.WORKING -> MaterialTheme.colorScheme.primary
        PomodoroState.SHORT_BREAK, PomodoroState.LONG_BREAK -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface


    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(200.dp) // Zamanlayıcı çemberinin boyutu
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Arka plan çemberi
            drawArc(
                color = surfaceVariantColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round),
                topLeft = Offset(10.dp.toPx(), 10.dp.toPx()), // Kenarlardan boşluk
                size = Size(size.width - 20.dp.toPx(), size.height - 20.dp.toPx())
            )
            // İlerleme çemberi
            drawArc(
                color = progressBarColor,
                startAngle = -90f,
                sweepAngle = animatedProgress * 360f,
                useCenter = false,
                style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round),
                topLeft = Offset(10.dp.toPx(), 10.dp.toPx()),
                size = Size(size.width - 20.dp.toPx(), size.height - 20.dp.toPx())
            )
        }

        val totalSeconds = timeMillis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        val timeText = String.format("%02d:%02d", minutes, seconds)

        // Metin uzunluğuna göre yazı boyutunu dinamik olarak ayarla
        val fontSize = when (timeText.length) {
            in 0..5 -> 60.sp // "MM:SS" formatı için normal boyut
            in 6..7 -> 50.sp // Daha uzunsa biraz küçült
            in 8..9 -> 40.sp // Daha da uzunsa daha da küçült
            else -> 32.sp // Çok uzunsa daha da küçük
        }

        Text(
            text = timeText,
            style = MaterialTheme.typography.displayLarge.copy(fontSize = fontSize), // Yazı boyutunu burada ayarla
            color = onSurfaceColor,
            textAlign = TextAlign.Center, // Metni ortala
            modifier = Modifier
                .align(Alignment.Center) // Box içinde merkezi hizalama
                .fillMaxSize() // Kutuyu doldur
                .wrapContentSize(Alignment.Center) // İçeriği kutu içinde ortala
        )
    }
}