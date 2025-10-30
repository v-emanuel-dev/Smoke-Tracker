package com.ivip.pomodorotimetrackerpro.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivip.pomodorotimetrackerpro.domain.model.SessionType
import com.ivip.pomodorotimetrackerpro.ui.theme.BreakColor
import com.ivip.pomodorotimetrackerpro.ui.theme.LongBreakColor
import com.ivip.pomodorotimetrackerpro.ui.theme.WorkColor
import com.ivip.pomodorotimetrackerpro.util.TimeUtils

@Composable
fun CircularTimer(
    currentTime: Long,
    totalTime: Long,
    sessionType: SessionType,
    modifier: Modifier = Modifier
) {
    val progress = if (totalTime > 0) currentTime.toFloat() / totalTime.toFloat() else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(300),
        label = "progress"
    )

    val timerColor = when (sessionType) {
        SessionType.WORK -> WorkColor
        SessionType.SHORT_BREAK -> BreakColor
        SessionType.LONG_BREAK -> LongBreakColor
    }

    Box(
        modifier = modifier.aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 20.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2

            // Background circle
            drawCircle(
                color = Color.Gray.copy(alpha = 0.2f),
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth)
            )

            // Progress arc
            val sweepAngle = 360 * animatedProgress
            drawArc(
                color = timerColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(
                    x = (size.width - radius * 2) / 2,
                    y = (size.height - radius * 2) / 2
                ),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = TimeUtils.formatMillisToTime(currentTime),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 64.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = when (sessionType) {
                    SessionType.WORK -> "Foco"
                    SessionType.SHORT_BREAK -> "Pausa Curta"
                    SessionType.LONG_BREAK -> "Pausa Longa"
                },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
