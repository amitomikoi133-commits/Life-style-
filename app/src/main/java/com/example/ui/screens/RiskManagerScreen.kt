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
import com.example.ui.components.MetricTile
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.TraderLifeViewModel
import kotlin.math.abs

@Composable
fun RiskManagerScreen(
    viewModel: TraderLifeViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.userSettings.collectAsStateWithLifecycle()
    val stats by viewModel.dashboardStats.collectAsStateWithLifecycle()

    var demoBalanceStr by remember(settings) { mutableStateOf(settings.demoBalance.toString()) }
    var riskPerTradePctStr by remember(settings) { mutableStateOf(settings.riskPerTradePct.toString()) }
    var maxDailyLossStr by remember(settings) { mutableStateOf(settings.maxDailyLoss.toString()) }
    var maxTradesStr by remember(settings) { mutableStateOf(settings.maxTradesPerDay.toString()) }

    // Position Size Calculator States
    var calcEntryStr by remember { mutableStateOf("1.08500") }
    var calcStopLossStr by remember { mutableStateOf("1.08200") }
    var calcTakeProfitStr by remember { mutableStateOf("1.09100") }
    var calcPipValueStr by remember { mutableStateOf("10.0") } // $10 per pip for 1 standard lot

    // Calculations
    val demoBal = demoBalanceStr.toDoubleOrNull() ?: settings.demoBalance
    val riskPct = riskPerTradePctStr.toDoubleOrNull() ?: settings.riskPerTradePct
    val maxLoss = maxDailyLossStr.toDoubleOrNull() ?: settings.maxDailyLoss
    val maxTrades = maxTradesStr.toIntOrNull() ?: settings.maxTradesPerDay

    val singleTradeRiskAmt = demoBal * (riskPct / 100.0)
    val maxTotalDailyRisk = singleTradeRiskAmt * maxTrades

    // Calculator results
    val calcEntry = calcEntryStr.toDoubleOrNull() ?: 1.08500
    val calcSL = calcStopLossStr.toDoubleOrNull() ?: 1.08200
    val calcTP = calcTakeProfitStr.toDoubleOrNull() ?: 1.09100
    val slDistance = abs(calcEntry - calcSL)
    val tpDistance = abs(calcTP - calcEntry)
    val rrRatio = if (slDistance > 0.000001) tpDistance / slDistance else 0.0
    val rewardAmount = singleTradeRiskAmt * rrRatio

    // Approximate lot size for forex (where distance in pips = slDistance * 10000)
    val pipsAtRisk = slDistance * 10000
    val calculatedLotSize = if (pipsAtRisk > 0) (singleTradeRiskAmt / (pipsAtRisk * 10.0)) else 0.0

    val isRiskExceeded = singleTradeRiskAmt > maxLoss

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
    ) {
        // Disclaimer Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, InfoBlue.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = InfoBlueBg),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = InfoBlue)
                    Text(
                        text = "Risk Management & Discipline Tool: This calculates risk and enforces habits. It does not execute live orders or connect to broker APIs.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Daily Risk Status Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        when (stats.riskStatus) {
                            "SAFE" -> ProfitGreen.copy(alpha = 0.5f)
                            "CAUTION" -> WarningAmber.copy(alpha = 0.5f)
                            else -> LossRed.copy(alpha = 0.6f)
                        },
                        RoundedCornerShape(16.dp)
                    ),
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
                            Text("Daily Risk Status", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                            Text(
                                text = stats.riskStatus,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = when (stats.riskStatus) {
                                    "SAFE" -> ProfitGreen
                                    "CAUTION" -> WarningAmber
                                    else -> LossRed
                                }
                            )
                        }
                        StatusBadge(
                            text = if (stats.riskStatus == "SAFE") "DISCIPLINED" else "ATTENTION NEEDED",
                            color = if (stats.riskStatus == "SAFE") ProfitGreen else LossRed
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Today's P/L", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                            Text(
                                text = "${settings.currency}${String.format("%.2f", stats.todayPnl)}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (stats.todayPnl >= 0) ProfitGreen else LossRed
                            )
                        }
                        Column {
                            Text("Max Daily Loss", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                            Text(
                                text = "${settings.currency}${settings.maxDailyLoss}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Column {
                            Text("Trades Done", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                            Text(
                                text = "${stats.todayTradesCount} / ${settings.maxTradesPerDay}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (stats.isOvertrading) LossRed else TextPrimary
                            )
                        }
                    }
                }
            }
        }

        // Risk Parameters Settings Card
        item {
            GlowCard(borderColor = BorderSubtle) {
                Text(
                    text = "Personal Risk Limits & Parameters",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccentLight
                )
                Text(
                    text = "Configure your demo capital and strict risk rules.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = demoBalanceStr,
                        onValueChange = { demoBalanceStr = it },
                        label = { Text("Demo Balance (${settings.currency})", color = TextSecondary) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                    OutlinedTextField(
                        value = riskPerTradePctStr,
                        onValueChange = { riskPerTradePctStr = it },
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
                        value = maxDailyLossStr,
                        onValueChange = { maxDailyLossStr = it },
                        label = { Text("Max Daily Loss (${settings.currency})", color = TextSecondary) },
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

                Spacer(modifier = Modifier.height(12.dp))

                // Calculated values display
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCardDark),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Automatic 1 Trade Risk Amount:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            Text("${settings.currency}${String.format("%.2f", singleTradeRiskAmt)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = GoldAccentLight)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Max Possible Daily Risk (if all loss):", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            Text("${settings.currency}${String.format("%.2f", maxTotalDailyRisk)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = if (maxTotalDailyRisk > maxLoss) LossRed else TextPrimary)
                        }
                    }
                }

                if (isRiskExceeded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "⚠️ Warning: Single trade risk (${settings.currency}${singleTradeRiskAmt}) exceeds your Max Daily Loss limit (${settings.currency}${maxLoss})!",
                        color = LossRed,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val updated = settings.copy(
                            demoBalance = demoBal,
                            riskPerTradePct = riskPct,
                            maxDailyLoss = maxLoss,
                            maxTradesPerDay = maxTrades
                        )
                        viewModel.updateSettings(updated)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_risk_settings_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                ) {
                    Text("Save Risk Limits", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Live Position & Lot Size Calculator
        item {
            GlowCard(borderColor = BorderSubtle) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Position Size & R:R Calculator",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = InfoBlue
                    )
                    Icon(Icons.Default.Calculate, contentDescription = null, tint = InfoBlue)
                }
                Text(
                    text = "Calculate exact lot size and R:R ratio before taking demo trade.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = calcEntryStr,
                        onValueChange = { calcEntryStr = it },
                        label = { Text("Entry Price", color = TextSecondary) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                    OutlinedTextField(
                        value = calcStopLossStr,
                        onValueChange = { calcStopLossStr = it },
                        label = { Text("Stop Loss", color = TextSecondary) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                    OutlinedTextField(
                        value = calcTakeProfitStr,
                        onValueChange = { calcTakeProfitStr = it },
                        label = { Text("Take Profit", color = TextSecondary) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Calculator Output Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCardHighlight),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Planned Risk ($riskPct%):", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                            Text("${settings.currency}${String.format("%.2f", singleTradeRiskAmt)}", fontWeight = FontWeight.Bold, color = LossRed)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Potential Reward:", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                            Text("${settings.currency}${String.format("%.2f", rewardAmount)}", fontWeight = FontWeight.Bold, color = ProfitGreen)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Risk to Reward (R:R):", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                            Text("1 : ${String.format("%.2f", rrRatio)}", fontWeight = FontWeight.ExtraBold, color = if (rrRatio >= 2.0) ProfitGreen else GoldAccentLight)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Estimated Lot Size (Forex 100k):", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                            Text("${String.format("%.2f", calculatedLotSize.coerceAtLeast(0.01))} Lots", fontWeight = FontWeight.Bold, color = InfoBlue)
                        }
                    }
                }
            }
        }
    }
}
