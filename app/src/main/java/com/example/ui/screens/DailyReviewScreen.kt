package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DailyReview
import com.example.ui.components.GlowCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.TraderLifeViewModel
import com.example.util.DateUtils

@Composable
fun DailyReviewScreen(
    viewModel: TraderLifeViewModel,
    modifier: Modifier = Modifier
) {
    val allReviews by viewModel.allReviews.collectAsStateWithLifecycle()
    val settings by viewModel.userSettings.collectAsStateWithLifecycle()
    val today = DateUtils.getTodayString()

    val currentTodayReview = allReviews.find { it.dateString == today }

    var didWell by remember(currentTodayReview) { mutableStateOf(currentTodayReview?.didWell ?: "") }
    var biggestMistake by remember(currentTodayReview) { mutableStateOf(currentTodayReview?.biggestMistake ?: "") }
    var mistakeReason by remember(currentTodayReview) { mutableStateOf(currentTodayReview?.mistakeReason ?: "") }
    var improvement by remember(currentTodayReview) { mutableStateOf(currentTodayReview?.improvement ?: "") }
    var lesson by remember(currentTodayReview) { mutableStateOf(currentTodayReview?.lesson ?: "") }
    var disciplineScore by remember(currentTodayReview) { mutableIntStateOf(currentTodayReview?.disciplineScore ?: 9) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
    ) {
        // Today's Review Form
        item {
            GlowCard(borderColor = GoldAccent.copy(alpha = 0.4f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (settings.language == "bn") "আজকের আত্ম-পর্যালোচনা" else "End-of-Day Journal & Review",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccentLight
                        )
                        Text(
                            text = DateUtils.formatDisplayDate(today),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    StatusBadge(
                        text = if (currentTodayReview != null) "SAVED" else "PENDING",
                        color = if (currentTodayReview != null) ProfitGreen else WarningAmber
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Discipline Score Slider (1-10)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Overall Discipline Rating:", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    Text("$disciplineScore / 10", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GoldAccent)
                }

                Slider(
                    value = disciplineScore.toFloat(),
                    onValueChange = { disciplineScore = it.toInt() },
                    valueRange = 1f..10f,
                    steps = 8,
                    colors = SliderDefaults.colors(
                        thumbColor = GoldAccent,
                        activeTrackColor = GoldAccent,
                        inactiveTrackColor = SurfaceCardHighlight
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 5 Questions
                OutlinedTextField(
                    value = didWell,
                    onValueChange = { didWell = it },
                    label = { Text("What did I do well today? (আজ কি ভালো করেছি?)", color = TextSecondary) },
                    placeholder = { Text("e.g. Followed risk rules, waited for confluence, stayed calm", color = TextTertiary) },
                    modifier = Modifier.fillMaxWidth().testTag("review_did_well_input"),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = biggestMistake,
                    onValueChange = { biggestMistake = it },
                    label = { Text("Biggest mistake today (আজকের সবচেয়ে বড় ভুল)", color = TextSecondary) },
                    placeholder = { Text("e.g. Sized up too high on second trade, checked phone during session", color = TextTertiary) },
                    modifier = Modifier.fillMaxWidth().testTag("review_biggest_mistake_input"),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = mistakeReason,
                    onValueChange = { mistakeReason = it },
                    label = { Text("Why did I make that mistake? (কেন ভুলটি হলো?)", color = TextSecondary) },
                    placeholder = { Text("e.g. Felt bored, wanted quick dopamine, skipped routine", color = TextTertiary) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = improvement,
                    onValueChange = { improvement = it },
                    label = { Text("What will I improve tomorrow? (আগামীকাল কি উন্নত করবো?)", color = TextSecondary) },
                    placeholder = { Text("e.g. Take only 1 setup, leave desk immediately after closing", color = TextTertiary) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = lesson,
                    onValueChange = { lesson = it },
                    label = { Text("Today's Key Lesson (আজকের শিক্ষা)", color = TextSecondary) },
                    placeholder = { Text("e.g. Preservation of capital is primary; profit is secondary", color = TextTertiary) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        val review = DailyReview(
                            dateString = today,
                            didWell = didWell.trim(),
                            biggestMistake = biggestMistake.trim(),
                            mistakeReason = mistakeReason.trim(),
                            improvement = improvement.trim(),
                            lesson = lesson.trim(),
                            disciplineScore = disciplineScore
                        )
                        viewModel.saveDailyReview(review)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_daily_review_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                ) {
                    Text("Save Daily Review", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Past Reviews History
        item {
            Text(
                text = "Past Reflections & Daily Reviews",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        val pastReviews = allReviews.filter { it.dateString != today }
        if (pastReviews.isEmpty()) {
            item {
                Text(
                    text = "No past reviews logged yet. Your daily entries will be archived here.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )
            }
        } else {
            items(pastReviews, key = { it.dateString }) { review ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCardDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = DateUtils.formatDisplayDate(review.dateString),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccentLight
                            )
                            StatusBadge(text = "Score: ${review.disciplineScore}/10", color = if (review.disciplineScore >= 8) ProfitGreen else WarningAmber)
                        }

                        if (review.didWell.isNotBlank()) {
                            Text("Did Well: ${review.didWell}", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                        }
                        if (review.biggestMistake.isNotBlank()) {
                            Text("Mistake: ${review.biggestMistake}", style = MaterialTheme.typography.bodySmall, color = LossRedLight)
                        }
                        if (review.lesson.isNotBlank()) {
                            Text("Lesson: “${review.lesson}”", style = MaterialTheme.typography.bodySmall, color = InfoBlue)
                        }
                    }
                }
            }
        }
    }
}
