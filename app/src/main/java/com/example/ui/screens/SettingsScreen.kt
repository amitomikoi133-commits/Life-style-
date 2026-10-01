package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.UserSettings
import com.example.ui.components.GlowCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.TraderLifeViewModel

@Composable
fun SettingsScreen(
    viewModel: TraderLifeViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.userSettings.collectAsStateWithLifecycle()

    var name by remember(settings) { mutableStateOf(settings.traderName) }
    var currency by remember(settings) { mutableStateOf(settings.currency) }
    var monthlyTargetStr by remember(settings) { mutableStateOf(settings.currentMonthTarget.toString()) }
    var riskLimitStr by remember(settings) { mutableStateOf(settings.riskPerTradePct.toString()) }
    var dailyLossLimitStr by remember(settings) { mutableStateOf(settings.maxDailyLoss.toString()) }
    var maxTradesStr by remember(settings) { mutableStateOf(settings.maxTradesPerDay.toString()) }
    var language by remember(settings) { mutableStateOf(settings.language) }
    var pinCode by remember(settings) { mutableStateOf(settings.pinCode) }
    var isPinLocked by remember(settings) { mutableStateOf(settings.isPinLocked) }

    var showResetConfirm by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
    ) {
        // Profile Settings
        item {
            GlowCard(borderColor = BorderSubtle) {
                Text(
                    text = "Trader Profile & Display",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccentLight
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Trader Name / Tag", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth().testTag("settings_trader_name"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Currency selector
                Text("Account Currency Symbol", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val currencies = listOf("$", "৳", "€", "£", "₹")
                    currencies.forEach { curr ->
                        FilterChip(
                            selected = currency == curr,
                            onClick = { currency = curr },
                            label = { Text(curr, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldAccent,
                                selectedLabelColor = Color.Black
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Trading Risk & Roadmap Parameters
        item {
            GlowCard(borderColor = BorderSubtle) {
                Text(
                    text = "Trading & Risk Rules Defaults",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ProfitGreenLight
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = monthlyTargetStr,
                        onValueChange = { monthlyTargetStr = it },
                        label = { Text("Month Target ($currency/day)", color = TextSecondary) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                    OutlinedTextField(
                        value = riskLimitStr,
                        onValueChange = { riskLimitStr = it },
                        label = { Text("Risk / Trade %", color = TextSecondary) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = dailyLossLimitStr,
                        onValueChange = { dailyLossLimitStr = it },
                        label = { Text("Daily Loss Limit ($currency)", color = TextSecondary) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                    OutlinedTextField(
                        value = maxTradesStr,
                        onValueChange = { maxTradesStr = it },
                        label = { Text("Max Trades / Day", color = TextSecondary) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                }
            }
        }

        // Language & UI Preferences
        item {
            GlowCard(borderColor = BorderSubtle) {
                Text(
                    text = "Language / ভাষা নির্বাচন",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = InfoBlue
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = language == "both",
                        onClick = { language = "both" },
                        label = { Text("Bilingual (উভয়)", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GoldAccent, selectedLabelColor = Color.Black),
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = language == "en",
                        onClick = { language = "en" },
                        label = { Text("English", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GoldAccent, selectedLabelColor = Color.Black),
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = language == "bn",
                        onClick = { language = "bn" },
                        label = { Text("বাংলা (Bengali)", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GoldAccent, selectedLabelColor = Color.Black),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Privacy & Journal Protection (Local Security)
        item {
            GlowCard(borderColor = BorderSubtle) {
                Text(
                    text = "Journal Privacy & PIN Protection",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Your journal is completely private and saved in local encrypted database storage.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Enable Quick PIN Lock", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    Switch(
                        checked = isPinLocked,
                        onCheckedChange = { isPinLocked = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = GoldAccent)
                    )
                }

                if (isPinLocked) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = pinCode,
                        onValueChange = { if (it.length <= 6) pinCode = it },
                        label = { Text("4-6 Digit Security PIN", color = TextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                }
            }
        }

        // Save Button
        item {
            Button(
                onClick = {
                    val updated = settings.copy(
                        traderName = name.trim(),
                        currency = currency,
                        currentMonthTarget = monthlyTargetStr.toDoubleOrNull() ?: settings.currentMonthTarget,
                        riskPerTradePct = riskLimitStr.toDoubleOrNull() ?: settings.riskPerTradePct,
                        maxDailyLoss = dailyLossLimitStr.toDoubleOrNull() ?: settings.maxDailyLoss,
                        maxTradesPerDay = maxTradesStr.toIntOrNull() ?: settings.maxTradesPerDay,
                        language = language,
                        pinCode = pinCode,
                        isPinLocked = isPinLocked
                    )
                    viewModel.updateSettings(updated)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("save_all_settings_button"),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save All Settings", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
