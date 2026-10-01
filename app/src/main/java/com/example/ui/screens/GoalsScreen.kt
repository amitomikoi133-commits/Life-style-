package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.MonthlyGoal
import com.example.ui.components.GlowCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.TraderLifeViewModel
import com.example.util.DateUtils
import kotlin.math.roundToInt

@Composable
fun GoalsScreen(
    viewModel: TraderLifeViewModel,
    modifier: Modifier = Modifier
) {
    val goals by viewModel.allGoals.collectAsStateWithLifecycle()
    val settings by viewModel.userSettings.collectAsStateWithLifecycle()
    val stats by viewModel.dashboardStats.collectAsStateWithLifecycle()
    val allTrades by viewModel.allTrades.collectAsStateWithLifecycle()

    val currentMonthNum = DateUtils.getCurrentMonthNumber()
    val currentGoal = goals.find { it.monthNumber == currentMonthNum } ?: MonthlyGoal(monthNumber = currentMonthNum, targetPerDay = settings.currentMonthTarget)

    val remainingDays = DateUtils.getRemainingDaysInCurrentMonth()
    val daysInMonth = DateUtils.getDaysInCurrentMonth()
    val totalMonthTarget = currentGoal.targetPerDay * daysInMonth
    val actualTotalPnl = allTrades.sumOf { it.pnl }
    val actualDailyAvg = if (daysInMonth > 0) actualTotalPnl / daysInMonth else 0.0
    val progressPct = if (totalMonthTarget > 0) (actualTotalPnl / totalMonthTarget).toFloat().coerceIn(0f, 2f) else 0f

    var goalToEdit by remember { mutableStateOf<MonthlyGoal?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
    ) {
        // Current Month Progress Spotlight Card
        item {
            GlowCard(borderColor = GoldAccent.copy(alpha = 0.5f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Current Month Roadmap (Month $currentMonthNum)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccentLight
                        )
                        Text(
                            text = "Target: ${settings.currency}${String.format("%.2f", currentGoal.targetPerDay)} / day (${settings.currency}${String.format("%.0f", totalMonthTarget)} total)",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    StatusBadge(
                        text = "$remainingDays DAYS LEFT",
                        color = GoldAccent
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Actual Daily Avg", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                        Text(
                            text = "${if (actualDailyAvg >= 0) "+" else ""}${settings.currency}${String.format("%.2f", actualDailyAvg)}/day",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (actualDailyAvg >= 0) ProfitGreen else LossRed
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Total Month P/L", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                        Text(
                            text = "${if (actualTotalPnl >= 0) "+" else ""}${settings.currency}${String.format("%.2f", actualTotalPnl)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (actualTotalPnl >= 0) ProfitGreen else LossRed
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Goal Progress", style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.sp)
                    Text("${(progressPct * 100).roundToInt()}%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = GoldAccentLight, fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(4.dp))

                LinearProgressIndicator(
                    progress = { progressPct.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (actualTotalPnl >= 0) ProfitGreen else LossRed,
                    trackColor = SurfaceCardHighlight
                )
            }
        }

        // 12-Month Target Roadmap List
        item {
            Text(
                text = "12-Month Progressive Targets (Month 1 -> Month 12)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        items(goals, key = { it.monthNumber }) { goal ->
            val isCurrentMonth = goal.monthNumber == currentMonthNum
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (isCurrentMonth) GoldAccent else BorderSubtle,
                        RoundedCornerShape(14.dp)
                    )
                    .clickable { goalToEdit = goal },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrentMonth) SurfaceCardHighlight else SurfaceCardDark
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isCurrentMonth) GoldAccent.copy(alpha = 0.2f) else SurfaceDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "M${goal.monthNumber}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isCurrentMonth) GoldAccent else TextSecondary
                            )
                        }

                        Column {
                            Text(
                                text = "Month ${goal.monthNumber} Target: ${settings.currency}${goal.targetPerDay} / day",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = goal.notes,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextTertiary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = { goalToEdit = goal }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Goal", tint = GoldAccent, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }

    // Edit Goal Dialog
    if (goalToEdit != null) {
        var targetStr by remember { mutableStateOf(goalToEdit!!.targetPerDay.toString()) }
        var notesStr by remember { mutableStateOf(goalToEdit!!.notes) }

        AlertDialog(
            onDismissRequest = { goalToEdit = null },
            title = { Text("Edit Month ${goalToEdit!!.monthNumber} Target", color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = targetStr,
                        onValueChange = { targetStr = it },
                        label = { Text("Target Per Day (${settings.currency})", color = TextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                    OutlinedTextField(
                        value = notesStr,
                        onValueChange = { notesStr = it },
                        label = { Text("Focus / Notes", color = TextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val targetVal = targetStr.toDoubleOrNull() ?: goalToEdit!!.targetPerDay
                        val updated = goalToEdit!!.copy(
                            targetPerDay = targetVal,
                            notes = notesStr.trim()
                        )
                        viewModel.updateMonthlyGoal(updated)
                        // If it's current month, sync userSettings target
                        if (goalToEdit!!.monthNumber == currentMonthNum) {
                            viewModel.updateSettings(settings.copy(currentMonthTarget = targetVal))
                        }
                        goalToEdit = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                ) {
                    Text("Save", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { goalToEdit = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }
}
