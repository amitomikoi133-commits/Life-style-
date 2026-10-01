package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Trade
import com.example.ui.theme.*
import kotlin.math.abs
import kotlin.math.max

@Composable
fun PnlBarChart(
    trades: List<Trade>,
    modifier: Modifier = Modifier,
    currency: String = "$"
) {
    if (trades.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(SurfaceCardDark, RoundedCornerShape(12.dp))
                .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No trade data to display yet",
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary
            )
        }
        return
    }

    // Take the last 10 trades in chronological order
    val recent = trades.take(10).reversed()
    val maxPnl = max(recent.maxOfOrNull { abs(it.pnl) } ?: 100.0, 50.0).toFloat()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCardDark)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Trades P/L ($currency)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
                Text(
                    text = "Last ${recent.size} Trades",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                if (size.width <= 0 || size.height <= 0) return@Canvas
                val w = size.width
                val h = size.height
                val midY = h / 2f
                val count = recent.size
                val barSpacing = w / (count.coerceAtLeast(1) + 1)
                val barWidth = (barSpacing * 0.55f).coerceIn(6f, 28f)

                // Draw center zero line
                drawLine(
                    color = BorderHighlight,
                    start = Offset(0f, midY),
                    end = Offset(w, midY),
                    strokeWidth = 1.5f
                )

                recent.forEachIndexed { i, trade ->
                    val x = barSpacing * (i + 1) - (barWidth / 2f)
                    val pnl = trade.pnl.toFloat()
                    val pnlRatio = (abs(pnl) / maxPnl).coerceIn(0.05f, 1f)
                    val barH = ((h / 2f - 10f) * pnlRatio).coerceAtLeast(4f)

                    if (pnl >= 0) {
                        // Profit bar (upward from midY)
                        drawRoundRect(
                            color = ProfitGreen,
                            topLeft = Offset(x, (midY - barH).coerceAtLeast(2f)),
                            size = Size(barWidth, barH),
                            cornerRadius = CornerRadius(4f, 4f)
                        )
                    } else {
                        // Loss bar (downward from midY)
                        drawRoundRect(
                            color = LossRed,
                            topLeft = Offset(x, midY),
                            size = Size(barWidth, barH),
                            cornerRadius = CornerRadius(4f, 4f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WinLossDonutChart(
    wins: Int,
    losses: Int,
    breakevens: Int,
    modifier: Modifier = Modifier
) {
    val total = (wins + losses + breakevens).coerceAtLeast(1)
    val winSweep = (wins.toFloat() / total) * 360f
    val lossSweep = (losses.toFloat() / total) * 360f
    val beSweep = (breakevens.toFloat() / total) * 360f

    val winRate = if (wins + losses > 0) ((wins.toDouble() / (wins + losses)) * 100).toInt() else 0

    Card(
        modifier = modifier.border(1.dp, BorderSubtle, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCardDark)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Win / Loss Breakdown",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier.size(110.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    if (size.width <= 0 || size.height <= 0) return@Canvas
                    val strokeWidth = 14.dp.toPx()
                    val diameter = (size.minDimension - strokeWidth).coerceAtLeast(10f)
                    val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
                    val arcSize = Size(diameter, diameter)

                    if (wins == 0 && losses == 0 && breakevens == 0) {
                        drawArc(
                            color = BorderHighlight,
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(strokeWidth, cap = StrokeCap.Round)
                        )
                    } else {
                        var startAngle = -90f
                        if (wins > 0) {
                            drawArc(
                                color = ProfitGreen,
                                startAngle = startAngle,
                                sweepAngle = (winSweep - 4f).coerceAtLeast(2f),
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(strokeWidth, cap = StrokeCap.Round)
                            )
                            startAngle += winSweep
                        }
                        if (losses > 0) {
                            drawArc(
                                color = LossRed,
                                startAngle = startAngle,
                                sweepAngle = (lossSweep - 4f).coerceAtLeast(2f),
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(strokeWidth, cap = StrokeCap.Round)
                            )
                            startAngle += lossSweep
                        }
                        if (breakevens > 0) {
                            drawArc(
                                color = WarningAmber,
                                startAngle = startAngle,
                                sweepAngle = (beSweep - 4f).coerceAtLeast(2f),
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$winRate%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (winRate >= 50) ProfitGreen else TextPrimary
                    )
                    Text(
                        text = "Win Rate",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary,
                        fontSize = 9.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(8.dp).background(ProfitGreen, RoundedCornerShape(2.dp)))
                    Text("Win: $wins", style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(8.dp).background(LossRed, RoundedCornerShape(2.dp)))
                    Text("Loss: $losses", style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(8.dp).background(WarningAmber, RoundedCornerShape(2.dp)))
                    Text("BE: $breakevens", style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun DisciplineRadialMeter(
    score: Int,
    modifier: Modifier = Modifier
) {
    val meterColor = when {
        score >= 80 -> ProfitGreen
        score >= 60 -> WarningAmber
        else -> LossRed
    }

    Box(
        modifier = modifier.size(100.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (size.width <= 0 || size.height <= 0) return@Canvas
            val strokeWidth = 10.dp.toPx()
            val diameter = (size.minDimension - strokeWidth).coerceAtLeast(10f)
            val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
            val arcSize = Size(diameter, diameter)

            // Background arc (240 degrees)
            drawArc(
                color = BorderHighlight,
                startAngle = 150f,
                sweepAngle = 240f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )

            // Foreground progress
            val progressSweep = (score / 100f) * 240f
            drawArc(
                color = meterColor,
                startAngle = 150f,
                sweepAngle = progressSweep.coerceAtLeast(4f),
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$score",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = meterColor
            )
            Text(
                text = "/ 100",
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary,
                fontSize = 10.sp
            )
        }
    }
}
