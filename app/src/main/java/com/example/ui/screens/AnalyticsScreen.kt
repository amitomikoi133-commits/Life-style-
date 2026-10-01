package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.TraderLifeViewModel
import kotlin.math.abs

@Composable
fun AnalyticsScreen(
    viewModel: TraderLifeViewModel,
    modifier: Modifier = Modifier
) {
    val trades by viewModel.allTrades.collectAsStateWithLifecycle()
    val settings by viewModel.userSettings.collectAsStateWithLifecycle()
    val habits by viewModel.activeHabits.collectAsStateWithLifecycle()
    val allHabitLogs by viewModel.allHabitLogs.collectAsStateWithLifecycle()
    val allSalah by viewModel.allSalah.collectAsStateWithLifecycle()

    val totalTrades = trades.size
    val winningTrades = trades.filter { it.result == "WIN" }
    val losingTrades = trades.filter { it.result == "LOSS" }
    val breakevenTrades = trades.filter { it.result == "BREAKEVEN" }

    val winsCount = winningTrades.size
    val lossCount = losingTrades.size
    val beCount = breakevenTrades.size

    val winRate = if (winsCount + lossCount > 0) ((winsCount.toDouble() / (winsCount + lossCount)) * 100) else 0.0

    val totalPnl = trades.sumOf { it.pnl }
    val grossProfit = winningTrades.sumOf { it.pnl }
    val grossLoss = abs(losingTrades.sumOf { it.pnl })

    val avgWin = if (winsCount > 0) grossProfit / winsCount else 0.0
    val avgLoss = if (lossCount > 0) grossLoss / lossCount else 0.0
    val profitFactor = if (grossLoss > 0.0) grossProfit / grossLoss else if (grossProfit > 0) 9.99 else 0.0

    val fomoCount = trades.count { it.fomo }
    val revengeCount = trades.count { it.revenge }
    val overtradeCount = trades.count { it.overtrade }
    val offPlanCount = trades.count { !it.accordingToPlan }

    // Habit and Salah consistency
    val totalPossibleHabits = (allSalah.size.coerceAtLeast(1)) * habits.size.coerceAtLeast(1)
    val totalCompletedHabits = allHabitLogs.count { it.isCompleted }
    val habitConsistency = if (totalPossibleHabits > 0) ((totalCompletedHabits.toDouble() / totalPossibleHabits) * 100).toInt().coerceIn(0, 100) else 0

    val totalPrayersPossible = allSalah.size.coerceAtLeast(1) * 5
    val totalPrayersDone = allSalah.sumOf { it.completedCount() }
    val salahConsistency = if (totalPrayersPossible > 0) ((totalPrayersDone.toDouble() / totalPrayersPossible) * 100).toInt().coerceIn(0, 100) else 0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
    ) {
        // Overall Performance Summary Card
        item {
            GlowCard(borderColor = if (totalPnl >= 0) ProfitGreen.copy(alpha = 0.4f) else LossRed.copy(alpha = 0.4f)) {
                Text(
                    text = "All-Time Journal Analytics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccentLight
                )
                Text(
                    text = "Demo/Paper Trading Statistics & Habit Correlations",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Total Net P/L", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                        Text(
                            text = "${if (totalPnl >= 0) "+" else ""}${settings.currency}${String.format("%.2f", totalPnl)}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (totalPnl >= 0) ProfitGreen else LossRed
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Profit Factor", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                        Text(
                            text = String.format("%.2f", profitFactor),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (profitFactor >= 1.5) ProfitGreen else WarningAmber
                        )
                    }
                }
            }
        }

        // Charts: Bar chart & Win/Loss Donut
        item {
            PnlBarChart(trades = trades, currency = settings.currency)
        }

        item {
            WinLossDonutChart(wins = winsCount, losses = lossCount, breakevens = beCount)
        }

        // Key Trading Metrics Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricTile(
                    title = "Average Win",
                    value = "+${settings.currency}${String.format("%.2f", avgWin)}",
                    subtitle = "$winsCount winning trades",
                    icon = Icons.Default.TrendingUp,
                    iconColor = ProfitGreen,
                    valueColor = ProfitGreen,
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    title = "Average Loss",
                    value = "-${settings.currency}${String.format("%.2f", avgLoss)}",
                    subtitle = "$lossCount losing trades",
                    icon = Icons.Default.TrendingDown,
                    iconColor = LossRed,
                    valueColor = LossRed,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Psychological & Discipline Violations Card
        item {
            GlowCard(borderColor = LossRed.copy(alpha = 0.3f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Psychology & Rule Violations",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = LossRedLight
                    )
                    Icon(Icons.Default.Warning, contentDescription = null, tint = LossRed)
                }
                Text(
                    text = "Tracked habits that lead to unnecessary losses.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$fomoCount", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = if (fomoCount == 0) ProfitGreen else LossRed)
                        Text("FOMO Trades", style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$revengeCount", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = if (revengeCount == 0) ProfitGreen else LossRed)
                        Text("Revenge Trades", style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$overtradeCount", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = if (overtradeCount == 0) ProfitGreen else WarningAmber)
                        Text("Overtrades", style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$offPlanCount", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = if (offPlanCount == 0) ProfitGreen else LossRed)
                        Text("Off-Plan", style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }

        // Life Discipline & Consistency
        item {
            GlowCard(borderColor = BorderSubtle) {
                Text(
                    text = "Life Discipline & Spiritual Consistency",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = InfoBlue
                )
                Text(
                    text = "Correlation between daily structure and trading execution.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$habitConsistency%", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = ProfitGreen)
                        Text("Routine Habits", style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$salahConsistency%", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = GoldAccentLight)
                        Text("Salah Prayers", style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${((trades.count { it.accordingToPlan }.toDouble() / totalTrades.coerceAtLeast(1)) * 100).toInt()}%", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = InfoBlue)
                        Text("Plan Adherence", style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
