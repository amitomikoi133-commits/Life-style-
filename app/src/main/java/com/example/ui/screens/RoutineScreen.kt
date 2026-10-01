package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Habit
import com.example.ui.components.GlowCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.TraderLifeViewModel
import com.example.util.DateUtils
import kotlin.math.roundToInt

@Composable
fun RoutineScreen(
    viewModel: TraderLifeViewModel,
    modifier: Modifier = Modifier
) {
    val habits by viewModel.activeHabits.collectAsStateWithLifecycle()
    val allHabitLogs by viewModel.allHabitLogs.collectAsStateWithLifecycle()
    val settings by viewModel.userSettings.collectAsStateWithLifecycle()
    val today = DateUtils.getTodayString()

    val todayLogs = allHabitLogs.filter { it.dateString == today && it.isCompleted }
    val completedHabitIds = remember(todayLogs) { todayLogs.map { it.habitId }.toSet() }

    val completionRate = if (habits.isNotEmpty()) {
        (completedHabitIds.size.toFloat() / habits.size.toFloat()).coerceIn(0f, 1f)
    } else 0f

    var showAddHabitDialog by remember { mutableStateOf(false) }
    var habitToDelete by remember { mutableStateOf<Habit?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
    ) {
        // Daily Progress Summary Card
        item {
            GlowCard(borderColor = ProfitGreen.copy(alpha = 0.4f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (settings.language == "bn") "দৈনিক রুটিন ও অভ্যাস" else "Daily Routine & Habits",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ProfitGreenLight
                        )
                        Text(
                            text = "${completedHabitIds.size} of ${habits.size} completed today",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    Text(
                        text = "${(completionRate * 100).roundToInt()}%",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (completionRate >= 0.75f) ProfitGreen else GoldAccentLight
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { completionRate },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = ProfitGreen,
                    trackColor = SurfaceCardHighlight
                )
            }
        }

        // Header with Add button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Habits Checklist",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Button(
                    onClick = { showAddHabitDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Habit", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // Habit Items
        items(habits, key = { it.id }) { habit ->
            val isChecked = completedHabitIds.contains(habit.id)
            val icon = getHabitIcon(habit.iconKey)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (isChecked) ProfitGreen.copy(alpha = 0.5f) else BorderSubtle,
                        RoundedCornerShape(14.dp)
                    ),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isChecked) SurfaceCardHighlight else SurfaceCardDark
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isChecked) ProfitGreenBg else SurfaceDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isChecked) ProfitGreen else GoldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = habit.titleEn,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Medium,
                                color = if (isChecked) TextPrimary else TextPrimary
                            )
                            if (habit.titleBn.isNotBlank() && settings.language != "en") {
                                Text(
                                    text = habit.titleBn,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                viewModel.toggleHabit(habit.id, checked)
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = ProfitGreen,
                                uncheckedColor = TextTertiary
                            ),
                            modifier = Modifier.testTag("habit_checkbox_${habit.id}")
                        )

                        IconButton(onClick = { habitToDelete = habit }) {
                            Icon(Icons.Outlined.Delete, contentDescription = "Delete Habit", tint = TextTertiary, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }

    // Add Habit Dialog
    if (showAddHabitDialog) {
        var titleEn by remember { mutableStateOf("") }
        var titleBn by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Routine") }
        var iconKey by remember { mutableStateOf("alarm") }

        AlertDialog(
            onDismissRequest = { showAddHabitDialog = false },
            title = { Text("Add Custom Habit", color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = titleEn,
                        onValueChange = { titleEn = it },
                        label = { Text("Habit Name (English)", color = TextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                    OutlinedTextField(
                        value = titleBn,
                        onValueChange = { titleBn = it },
                        label = { Text("Habit Name (Bengali / বাংলা)", color = TextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category (e.g. Trading, Health, Mindset)", color = TextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (titleEn.isNotBlank()) {
                            viewModel.addCustomHabit(titleEn.trim(), titleBn.trim(), iconKey, category.trim())
                            showAddHabitDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                ) {
                    Text("Add", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddHabitDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }

    // Delete Habit Confirmation
    if (habitToDelete != null) {
        AlertDialog(
            onDismissRequest = { habitToDelete = null },
            title = { Text("Delete Habit?", color = TextPrimary) },
            text = { Text("Are you sure you want to delete '${habitToDelete!!.titleEn}'?", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        habitToDelete?.let { viewModel.deleteHabit(it) }
                        habitToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LossRed)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { habitToDelete = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }
}

fun getHabitIcon(key: String): ImageVector {
    return when (key.lowercase()) {
        "alarm" -> Icons.Default.Alarm
        "book" -> Icons.Default.MenuBook
        "chart" -> Icons.Default.CandlestickChart
        "trending_up" -> Icons.Default.TrendingUp
        "fitness" -> Icons.Default.FitnessCenter
        "reading" -> Icons.Default.AutoStories
        "family" -> Icons.Default.Favorite
        "bedtime" -> Icons.Default.Bedtime
        else -> Icons.Default.CheckCircleOutline
    }
}
