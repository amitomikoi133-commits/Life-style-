package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.TraderLifeViewModel
import com.example.util.DateUtils
import kotlin.math.roundToInt

@Composable
fun DashboardScreen(
    viewModel: TraderLifeViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.dashboardStats.collectAsStateWithLifecycle()
    val settings by viewModel.userSettings.collectAsStateWithLifecycle()
    val allTrades by viewModel.allTrades.collectAsStateWithLifecycle()
    val lang = settings.language

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp)
    ) {
        // Hero Banner Card with Trading Discipline Motto
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldAccent.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCardDark)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    SurfaceCardElevated,
                                    Color(0xFF1E2638),
                                    SurfaceCardDark
                                )
                            ),
                            RoundedCornerShape(16.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Welcome, ${settings.traderName}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Demo/Paper Trading Discipline Journal",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GoldAccentLight,
                                    fontSize = 12.sp
                                )
                            }
                            StatusBadge(
                                text = "DEMO JOURNAL",
                                color = GoldAccent
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "“Control your risk, protect your mind, stay patient.”",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = { viewModel.setScreen(AppScreen.JOURNAL) },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Log Trade", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Today's Target vs Actual P/L Hero Metric Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (stats.todayPnl >= 0) ProfitGreen.copy(alpha = 0.4f) else LossRed.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (lang == "bn") "আজকের ডেমো P/L ও টার্গেট" else "Today's Demo P/L vs Target",
                                style = MaterialTheme.typography.labelLarge,
                                color = TextSecondary
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "${if (stats.todayPnl >= 0) "+" else ""}${settings.currency}${String.format("%.2f", stats.todayPnl)}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (stats.todayPnl >= 0) ProfitGreen else LossRed
                                )
                                Text(
                                    text = "/ Target: ${settings.currency}${String.format("%.2f", stats.dailyTarget)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextTertiary
                                )
                            }
                        }
                        DisciplineRadialMeter(score = stats.disciplineScore)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Target progress bar
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Daily Target Progress",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "${(stats.targetProgressPct * 100).roundToInt()}%",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccentLight,
                                fontSize = 11.sp
                            )
                        }
                        LinearProgressIndicator(
                            progress = { stats.targetProgressPct.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (stats.todayPnl >= 0) ProfitGreen else LossRed,
                            trackColor = SurfaceCardHighlight
                        )
                    }
                }
            }
        }

        // 4 Quick KPI Tiles: Trades, Win Rate, Overtrading, Risk Status
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricTile(
                    title = "Trades Today",
                    value = "${stats.todayTradesCount}",
                    subtitle = "W: ${stats.todayWins} | L: ${stats.todayLosses}",
                    icon = Icons.Default.ShowChart,
                    iconColor = InfoBlue,
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    title = "Win Rate",
                    value = "${stats.winRate.roundToInt()}%",
                    subtitle = "Target: >50%",
                    icon = Icons.Default.TrendingUp,
                    iconColor = if (stats.winRate >= 50) ProfitGreen else WarningAmber,
                    valueColor = if (stats.winRate >= 50) ProfitGreen else TextPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricTile(
                    title = "Overtrade Check",
                    value = "${stats.todayTradesCount}/${stats.maxTradesAllowed}",
                    subtitle = if (stats.isOvertrading) "Exceeded Limit!" else "Within Safe Limit",
                    icon = if (stats.isOvertrading) Icons.Default.Warning else Icons.Default.CheckCircle,
                    iconColor = if (stats.isOvertrading) LossRed else ProfitGreen,
                    valueColor = if (stats.isOvertrading) LossRed else ProfitGreen,
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    title = "Daily Risk Status",
                    value = stats.riskStatus,
                    subtitle = "Max Loss: ${settings.currency}${stats.maxDailyLossAllowed.toInt()}",
                    icon = Icons.Default.Shield,
                    iconColor = when (stats.riskStatus) {
                        "SAFE" -> ProfitGreen
                        "CAUTION" -> WarningAmber
                        else -> LossRed
                    },
                    valueColor = when (stats.riskStatus) {
                        "SAFE" -> ProfitGreen
                        "CAUTION" -> WarningAmber
                        else -> LossRed
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Rule Violations Card
        if (stats.ruleViolationsCount > 0) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, LossRed.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = LossRedBg),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = LossRed)
                        Column {
                            Text(
                                text = "Rule Violations Detected: ${stats.ruleViolationsCount}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = LossRedLight
                            )
                            Text(
                                text = "Check for FOMO, revenge trading, or moving stop loss.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Salah Tracker & Routine Quick Summary Card
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                        .clickable { viewModel.setScreen(AppScreen.SALAH) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCardDark)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Salah", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                            Icon(Icons.Default.Mosque, contentDescription = null, tint = SalahFajr, modifier = Modifier.size(18.dp))
                        }
                        Text(
                            text = "${stats.salahCompletedCount}/5 Prayed",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (stats.salahCompletedCount >= 4) ProfitGreen else GoldAccentLight
                        )
                        Text(
                            text = "Jamaat: ${stats.salahJamaatCount}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextTertiary,
                            fontSize = 11.sp
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                        .clickable { viewModel.setScreen(AppScreen.ROUTINE) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCardDark)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Routine", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                            Icon(Icons.Default.Checklist, contentDescription = null, tint = ProfitGreen, modifier = Modifier.size(18.dp))
                        }
                        Text(
                            text = "${stats.routineCompletedCount}/${stats.routineTotalCount} Done",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (stats.routineCompletionPct >= 0.75f) ProfitGreen else GoldAccentLight
                        )
                        Text(
                            text = "${(stats.routineCompletionPct * 100).toInt()}% completed",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextTertiary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Monthly Progression Roadmap Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                    .clickable { viewModel.setScreen(AppScreen.GOALS) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Flag, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Monthly Target Roadmap",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccentLight
                            )
                        }
                        Text(
                            text = "Current Month Goal: ${settings.currency}${settings.currentMonthTarget}/day",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${DateUtils.getRemainingDaysInCurrentMonth()} days remaining this month",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextTertiary,
                            fontSize = 11.sp
                        )
                    }
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "View Roadmap",
                        tint = TextSecondary
                    )
                }
            }
        }

        // Recent PnL Bar Chart
        item {
            PnlBarChart(trades = allTrades, currency = settings.currency)
        }

        // Quick Bottom Action Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.setScreen(AppScreen.DAILY_REVIEW) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldAccentLight),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderHighlight)
                ) {
                    Icon(Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Daily Review", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = { viewModel.setScreen(AppScreen.PSYCHOLOGY) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = InfoBlue),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderHighlight)
                ) {
                    Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Psychology", fontSize = 12.sp)
                }
            }
        }
    }
}
