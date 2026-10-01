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
import com.example.data.model.PsychologyLog
import com.example.ui.components.GlowCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.TraderLifeViewModel
import com.example.util.DateUtils

@Composable
fun PsychologyScreen(
    viewModel: TraderLifeViewModel,
    modifier: Modifier = Modifier
) {
    val psychologyLogs by viewModel.allPsychologyLogs.collectAsStateWithLifecycle()
    val settings by viewModel.userSettings.collectAsStateWithLifecycle()

    var calm by remember { mutableIntStateOf(4) }
    var fear by remember { mutableIntStateOf(1) }
    var greed by remember { mutableIntStateOf(1) }
    var fomo by remember { mutableIntStateOf(1) }
    var revenge by remember { mutableIntStateOf(1) }
    var confidence by remember { mutableIntStateOf(4) }
    var stress by remember { mutableIntStateOf(2) }
    var note by remember { mutableStateOf("") }
    var sessionType by remember { mutableStateOf("Pre-Market") }

    // Pre-Market Checklist State
    var checkSleep by remember { mutableStateOf(true) }
    var checkPatience by remember { mutableStateOf(true) }
    var checkRiskAccepted by remember { mutableStateOf(true) }
    var checkNoDistraction by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
    ) {
        // Pre-Market Readiness Checklist
        item {
            GlowCard(borderColor = GoldAccent.copy(alpha = 0.3f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pre-Session Readiness Checklist",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccentLight
                    )
                    StatusBadge(
                        text = if (checkSleep && checkPatience && checkRiskAccepted && checkNoDistraction) "READY TO TRADE" else "CAUTION",
                        color = if (checkSleep && checkPatience && checkRiskAccepted && checkNoDistraction) ProfitGreen else WarningAmber
                    )
                }
                Text(
                    text = "Verify your mental clarity and emotional detachment before opening any charts.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = checkSleep, onCheckedChange = { checkSleep = it }, colors = CheckboxDefaults.colors(checkedColor = ProfitGreen))
                    Text("Well rested, energized & prayed on time", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = checkPatience, onCheckedChange = { checkPatience = it }, colors = CheckboxDefaults.colors(checkedColor = ProfitGreen))
                    Text("Completely detached from outcome, ready to wait for A+ setups", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = checkRiskAccepted, onCheckedChange = { checkRiskAccepted = it }, colors = CheckboxDefaults.colors(checkedColor = ProfitGreen))
                    Text("Pre-accepted 100% of defined risk per trade ($${settings.riskPerTradePct}%)", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = checkNoDistraction, onCheckedChange = { checkNoDistraction = it }, colors = CheckboxDefaults.colors(checkedColor = ProfitGreen))
                    Text("Quiet environment, no financial stress or rushed mindset", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                }
            }
        }

        // Real-time Emotion & Mindset Logger Form
        item {
            GlowCard(borderColor = BorderSubtle) {
                Text(
                    text = "Log Emotional State",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = InfoBlue
                )
                Text(
                    text = "Record your feelings (1 = Lowest, 5 = Highest).",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Session selector chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val sessions = listOf("Pre-Market", "Active-Session", "Post-Market", "Reflection")
                    sessions.forEach { sess ->
                        FilterChip(
                            selected = sessionType == sess,
                            onClick = { sessionType = sess },
                            label = { Text(sess, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldAccent,
                                selectedLabelColor = Color.Black
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Emotion Sliders
                EmotionSliderItem(label = "Calmness & Peace (শান্তভাব)", value = calm, onValueChange = { calm = it }, color = ProfitGreen)
                EmotionSliderItem(label = "Confidence & Clarity (আত্মবিশ্বাস)", value = confidence, onValueChange = { confidence = it }, color = InfoBlue)
                EmotionSliderItem(label = "Stress & Tension (চাপ)", value = stress, onValueChange = { stress = it }, color = WarningAmber)
                EmotionSliderItem(label = "Fear & Anxiety (ভয়)", value = fear, onValueChange = { fear = it }, color = LossRed)
                EmotionSliderItem(label = "Greed (লোভ)", value = greed, onValueChange = { greed = it }, color = LossRed)
                EmotionSliderItem(label = "FOMO (সুযোগ হারানোর ভয়)", value = fomo, onValueChange = { fomo = it }, color = LossRed)
                EmotionSliderItem(label = "Revenge Urge (ক্ষতিপূরণের জেদ)", value = revenge, onValueChange = { revenge = it }, color = LossRed)

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("What was going through your mind during the trade/session?", color = TextSecondary) },
                    placeholder = { Text("Write your thoughts, doubts, market observations...", color = TextTertiary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("psychology_note_input"),
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = SurfaceCardDark,
                        unfocusedContainerColor = SurfaceCardDark
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val log = PsychologyLog(
                            dateString = DateUtils.getTodayString(),
                            calm = calm,
                            fear = fear,
                            greed = greed,
                            fomo = fomo,
                            revenge = revenge,
                            confidence = confidence,
                            stress = stress,
                            mindsetNote = note.trim(),
                            sessionType = sessionType
                        )
                        viewModel.savePsychologyLog(log)
                        note = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_psychology_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                ) {
                    Text("Save Mindset Entry", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        // History of Mindset Logs
        item {
            Text(
                text = "Past Psychology & Emotion Logs",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        if (psychologyLogs.isEmpty()) {
            item {
                Text(
                    text = "No psychology records logged yet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )
            }
        } else {
            items(psychologyLogs, key = { it.id }) { log ->
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
                            StatusBadge(text = log.sessionType, color = GoldAccent)
                            Text(
                                text = DateUtils.formatMillisDate(log.timestamp),
                                style = MaterialTheme.typography.bodySmall,
                                color = TextTertiary,
                                fontSize = 11.sp
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Calm: ${log.calm}/5", style = MaterialTheme.typography.bodySmall, color = ProfitGreen, fontSize = 11.sp)
                            Text("Confidence: ${log.confidence}/5", style = MaterialTheme.typography.bodySmall, color = InfoBlue, fontSize = 11.sp)
                            Text("Stress: ${log.stress}/5", style = MaterialTheme.typography.bodySmall, color = WarningAmber, fontSize = 11.sp)
                            if (log.fomo > 2) Text("FOMO: ${log.fomo}/5", style = MaterialTheme.typography.bodySmall, color = LossRed, fontSize = 11.sp)
                        }

                        if (log.mindsetNote.isNotBlank()) {
                            Text(
                                text = "“${log.mindsetNote}”",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmotionSliderItem(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    color: Color
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
            Text("$value / 5", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = color)
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 1f..5f,
            steps = 3,
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color,
                inactiveTrackColor = SurfaceCardHighlight
            )
        )
    }
}
