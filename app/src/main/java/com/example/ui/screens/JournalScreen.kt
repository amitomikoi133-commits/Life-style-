package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.Trade
import com.example.ui.components.GlowCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.TraderLifeViewModel
import com.example.util.DateUtils
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    viewModel: TraderLifeViewModel,
    modifier: Modifier = Modifier
) {
    val trades by viewModel.filteredTrades.collectAsStateWithLifecycle()
    val settings by viewModel.userSettings.collectAsStateWithLifecycle()
    val searchQuery by viewModel.journalSearchQuery.collectAsStateWithLifecycle()
    val resultFilter by viewModel.journalFilterResult.collectAsStateWithLifecycle()
    val directionFilter by viewModel.journalFilterDirection.collectAsStateWithLifecycle()
    val mistakeOnly by viewModel.journalFilterMistakeOnly.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedTradeForDetail by remember { mutableStateOf<Trade?>(null) }
    var tradeToDelete by remember { mutableStateOf<Trade?>(null) }
    var tradeToEdit by remember { mutableStateOf<Trade?>(null) }

    Box(modifier = modifier.fillMaxSize().background(BackgroundDark)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.journalSearchQuery.value = it },
                placeholder = { Text("Search pair, setup, mistake, lesson...", color = TextTertiary) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = TextSecondary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.journalSearchQuery.value = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search", tint = TextSecondary)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_journal_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceCardDark,
                    unfocusedContainerColor = SurfaceCardDark,
                    focusedBorderColor = GoldAccent,
                    unfocusedBorderColor = BorderSubtle,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Result filter items
                val results = listOf("ALL", "WIN", "LOSS", "BREAKEVEN")
                items(results) { res ->
                    FilterChip(
                        selected = resultFilter == res,
                        onClick = { viewModel.journalFilterResult.value = res },
                        label = { Text(res) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = when (res) {
                                "WIN" -> ProfitGreen
                                "LOSS" -> LossRed
                                "BREAKEVEN" -> WarningAmber
                                else -> GoldAccent
                            },
                            selectedLabelColor = Color.Black,
                            containerColor = SurfaceCardDark,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = resultFilter == res,
                            borderColor = BorderSubtle,
                            selectedBorderColor = Color.Transparent
                        )
                    )
                }

                // Buy/Sell filter
                item {
                    FilterChip(
                        selected = directionFilter == "BUY",
                        onClick = {
                            viewModel.journalFilterDirection.value = if (directionFilter == "BUY") "ALL" else "BUY"
                        },
                        label = { Text("BUY Only") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ProfitGreen,
                            selectedLabelColor = Color.Black,
                            containerColor = SurfaceCardDark,
                            labelColor = TextSecondary
                        )
                    )
                }

                item {
                    FilterChip(
                        selected = directionFilter == "SELL",
                        onClick = {
                            viewModel.journalFilterDirection.value = if (directionFilter == "SELL") "ALL" else "SELL"
                        },
                        label = { Text("SELL Only") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LossRed,
                            selectedLabelColor = Color.White,
                            containerColor = SurfaceCardDark,
                            labelColor = TextSecondary
                        )
                    )
                }

                item {
                    FilterChip(
                        selected = mistakeOnly,
                        onClick = { viewModel.journalFilterMistakeOnly.value = !mistakeOnly },
                        label = { Text("Mistakes/Violations") },
                        leadingIcon = {
                            Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LossRedBg,
                            selectedLabelColor = LossRedLight,
                            containerColor = SurfaceCardDark,
                            labelColor = TextSecondary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Journal List
            if (trades.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            Icons.Outlined.EditCalendar,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(54.dp)
                        )
                        Text(
                            text = "No journal entries found",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = "Log your paper trades to analyze psychology and discipline.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextTertiary
                        )
                        Button(
                            onClick = {
                                tradeToEdit = null
                                showAddDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Log First Trade", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(trades, key = { it.id }) { trade ->
                        TradeItemCard(
                            trade = trade,
                            currency = settings.currency,
                            onClick = { selectedTradeForDetail = trade },
                            onEdit = {
                                tradeToEdit = trade
                                showAddDialog = true
                            },
                            onDelete = { tradeToDelete = trade }
                        )
                    }
                }
            }
        }

        // Floating Action Button to Add Trade
        FloatingActionButton(
            onClick = {
                tradeToEdit = null
                showAddDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_trade_fab"),
            containerColor = GoldAccent,
            contentColor = Color.Black
        ) {
            Icon(Icons.Default.Add, contentDescription = "Log New Trade", modifier = Modifier.size(28.dp))
        }
    }

    // Add / Edit Trade Dialog
    if (showAddDialog) {
        TradeFormDialog(
            existingTrade = tradeToEdit,
            currency = settings.currency,
            defaultBalance = settings.demoBalance,
            defaultRiskPct = settings.riskPerTradePct,
            onDismiss = {
                showAddDialog = false
                tradeToEdit = null
            },
            onSave = { trade ->
                viewModel.saveTrade(trade)
                showAddDialog = false
                tradeToEdit = null
            }
        )
    }

    // Trade Details Dialog
    if (selectedTradeForDetail != null) {
        TradeDetailDialog(
            trade = selectedTradeForDetail!!,
            currency = settings.currency,
            onDismiss = { selectedTradeForDetail = null },
            onEdit = {
                tradeToEdit = selectedTradeForDetail
                selectedTradeForDetail = null
                showAddDialog = true
            },
            onDelete = {
                tradeToDelete = selectedTradeForDetail
                selectedTradeForDetail = null
            }
        )
    }

    // Delete Confirmation Dialog
    if (tradeToDelete != null) {
        AlertDialog(
            onDismissRequest = { tradeToDelete = null },
            title = { Text("Delete Journal Entry?", color = TextPrimary) },
            text = {
                Text(
                    "Are you sure you want to delete trade on ${tradeToDelete!!.pair} (${tradeToDelete!!.direction})? This action cannot be undone.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        tradeToDelete?.let { viewModel.deleteTrade(it) }
                        tradeToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LossRed)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { tradeToDelete = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
fun TradeItemCard(
    trade: Trade,
    currency: String,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isProfit = trade.pnl >= 0
    val pnlColor = when (trade.result) {
        "WIN" -> ProfitGreen
        "LOSS" -> LossRed
        "BREAKEVEN" -> WarningAmber
        else -> TextSecondary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCardDark)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Pair, Buy/Sell, Date, PnL
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusBadge(
                        text = trade.direction,
                        color = if (trade.direction == "BUY") ProfitGreen else LossRed
                    )
                    Text(
                        text = trade.pair,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Text(
                    text = "${if (isProfit && trade.pnl > 0) "+" else ""}$currency${String.format("%.2f", trade.pnl)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = pnlColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Price Details: Entry, SL, TP, Setup
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Entry: ${trade.entryPrice} | SL: ${trade.stopLoss} | TP: ${trade.takeProfit}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary,
                    fontSize = 11.sp
                )
                Text(
                    text = trade.dateString,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Setup and Reason snippet
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SurfaceCardHighlight
                ) {
                    Text(
                        text = trade.setup,
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldAccentLight,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Tags for discipline violations if any
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (trade.fomo) {
                        StatusBadge(text = "FOMO", color = LossRed)
                    }
                    if (trade.revenge) {
                        StatusBadge(text = "REVENGE", color = LossRed)
                    }
                    if (trade.overtrade) {
                        StatusBadge(text = "OVERTRADE", color = WarningAmber)
                    }
                    if (!trade.accordingToPlan) {
                        StatusBadge(text = "OFF PLAN", color = LossRed)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeFormDialog(
    existingTrade: Trade?,
    currency: String,
    defaultBalance: Double,
    defaultRiskPct: Double,
    onDismiss: () -> Unit,
    onSave: (Trade) -> Unit
) {
    var pair by remember { mutableStateOf(existingTrade?.pair ?: "EUR/USD") }
    var direction by remember { mutableStateOf(existingTrade?.direction ?: "BUY") }
    var entryPriceStr by remember { mutableStateOf(existingTrade?.entryPrice?.toString() ?: "1.08500") }
    var stopLossStr by remember { mutableStateOf(existingTrade?.stopLoss?.toString() ?: "1.08200") }
    var takeProfitStr by remember { mutableStateOf(existingTrade?.takeProfit?.toString() ?: "1.09100") }
    var riskPercentStr by remember { mutableStateOf(existingTrade?.riskPercent?.toString() ?: defaultRiskPct.toString()) }
    var riskAmountStr by remember { mutableStateOf(existingTrade?.riskAmount?.toString() ?: (defaultBalance * (defaultRiskPct / 100.0)).toString()) }
    var result by remember { mutableStateOf(existingTrade?.result ?: "WIN") }
    var pnlStr by remember { mutableStateOf(existingTrade?.pnl?.toString() ?: "150.0") }
    var setup by remember { mutableStateOf(existingTrade?.setup ?: "Breakout Retest") }
    var reason by remember { mutableStateOf(existingTrade?.reason ?: "") }
    var emotionBefore by remember { mutableStateOf(existingTrade?.emotionBefore ?: "Calm") }
    var emotionDuring by remember { mutableStateOf(existingTrade?.emotionDuring ?: "Disciplined") }
    var accordingToPlan by remember { mutableStateOf(existingTrade?.accordingToPlan ?: true) }
    var fomo by remember { mutableStateOf(existingTrade?.fomo ?: false) }
    var revenge by remember { mutableStateOf(existingTrade?.revenge ?: false) }
    var overtrade by remember { mutableStateOf(existingTrade?.overtrade ?: false) }
    var mistake by remember { mutableStateOf(existingTrade?.mistake ?: "None") }
    var lesson by remember { mutableStateOf(existingTrade?.lesson ?: "") }
    var screenshotUri by remember { mutableStateOf(existingTrade?.screenshotUri) }
    var dateString by remember { mutableStateOf(existingTrade?.dateString ?: DateUtils.getTodayString()) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            screenshotUri = uri.toString()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = SurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderHighlight)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (existingTrade == null) "Log Paper Trade" else "Edit Trade Journal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccentLight
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                HorizontalDivider(color = BorderSubtle, modifier = Modifier.padding(vertical = 8.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Pair and Direction
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = pair,
                            onValueChange = { pair = it },
                            label = { Text("Pair / Asset", color = TextSecondary) },
                            modifier = Modifier.weight(1.2f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Direction", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Button(
                                    onClick = { direction = "BUY" },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (direction == "BUY") ProfitGreen else SurfaceCardDark
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(4.dp)
                                ) {
                                    Text("BUY", color = if (direction == "BUY") Color.Black else TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = { direction = "SELL" },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (direction == "SELL") LossRed else SurfaceCardDark
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(4.dp)
                                ) {
                                    Text("SELL", color = if (direction == "SELL") Color.White else TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Entry, SL, TP
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = entryPriceStr,
                            onValueChange = { entryPriceStr = it },
                            label = { Text("Entry Price", color = TextSecondary) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                        )
                        OutlinedTextField(
                            value = stopLossStr,
                            onValueChange = { stopLossStr = it },
                            label = { Text("Stop Loss", color = TextSecondary) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                        )
                        OutlinedTextField(
                            value = takeProfitStr,
                            onValueChange = { takeProfitStr = it },
                            label = { Text("Take Profit", color = TextSecondary) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                        )
                    }

                    // Risk & Result Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = riskPercentStr,
                            onValueChange = { riskPercentStr = it },
                            label = { Text("Risk %", color = TextSecondary) },
                            modifier = Modifier.weight(0.9f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                        )
                        OutlinedTextField(
                            value = riskAmountStr,
                            onValueChange = { riskAmountStr = it },
                            label = { Text("Risk ($currency)", color = TextSecondary) },
                            modifier = Modifier.weight(1.1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                        )
                        OutlinedTextField(
                            value = pnlStr,
                            onValueChange = { pnlStr = it },
                            label = { Text("P/L ($currency)", color = TextSecondary) },
                            modifier = Modifier.weight(1.2f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                        )
                    }

                    // Result Selector
                    Text("Result", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val results = listOf("WIN", "LOSS", "BREAKEVEN", "OPEN")
                        results.forEach { res ->
                            FilterChip(
                                selected = result == res,
                                onClick = { result = res },
                                label = { Text(res, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = when (res) {
                                        "WIN" -> ProfitGreen
                                        "LOSS" -> LossRed
                                        "BREAKEVEN" -> WarningAmber
                                        else -> InfoBlue
                                    },
                                    selectedLabelColor = Color.Black
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Trading Setup & Reason
                    OutlinedTextField(
                        value = setup,
                        onValueChange = { setup = it },
                        label = { Text("Trading Setup (e.g. Order Block, Liquidity Sweep, FVG)", color = TextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )

                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text("Reason for Entry (Confluences, Higher timeframe bias)", color = TextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )

                    // Emotions Before & During
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = emotionBefore,
                            onValueChange = { emotionBefore = it },
                            label = { Text("Before-Trade Emotion", color = TextSecondary) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                        )
                        OutlinedTextField(
                            value = emotionDuring,
                            onValueChange = { emotionDuring = it },
                            label = { Text("During-Trade Emotion", color = TextSecondary) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                        )
                    }

                    // Psychology & Discipline Checkboxes
                    Text("Discipline & Psychology Check", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = GoldAccentLight)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = accordingToPlan,
                            onCheckedChange = { accordingToPlan = it },
                            colors = CheckboxDefaults.colors(checkedColor = ProfitGreen)
                        )
                        Text("Trade was according to Trading Plan", color = TextPrimary, style = MaterialTheme.typography.bodyMedium)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Checkbox(checked = fomo, onCheckedChange = { fomo = it }, colors = CheckboxDefaults.colors(checkedColor = LossRed))
                            Text("FOMO?", color = TextPrimary, style = MaterialTheme.typography.bodySmall)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Checkbox(checked = revenge, onCheckedChange = { revenge = it }, colors = CheckboxDefaults.colors(checkedColor = LossRed))
                            Text("Revenge?", color = TextPrimary, style = MaterialTheme.typography.bodySmall)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Checkbox(checked = overtrade, onCheckedChange = { overtrade = it }, colors = CheckboxDefaults.colors(checkedColor = WarningAmber))
                            Text("Overtrade?", color = TextPrimary, style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    // Mistakes & Lessons
                    OutlinedTextField(
                        value = mistake,
                        onValueChange = { mistake = it },
                        label = { Text("Mistake Made (None, Chased price, Moved SL...)", color = TextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )

                    OutlinedTextField(
                        value = lesson,
                        onValueChange = { lesson = it },
                        label = { Text("Lesson Learned for Next Trade", color = TextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )

                    // Screenshot Upload Option
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Trade Screenshot", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                            Text(if (screenshotUri != null) "Screenshot attached" else "Attach chart analysis", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                        }
                        OutlinedButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldAccentLight)
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (screenshotUri == null) "Pick Image" else "Change")
                        }
                    }

                    if (screenshotUri != null) {
                        AsyncImage(
                            model = screenshotUri,
                            contentDescription = "Trade Screenshot Preview",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = LossRed,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Bottom Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            val entry = entryPriceStr.toDoubleOrNull()
                            val sl = stopLossStr.toDoubleOrNull()
                            val tp = takeProfitStr.toDoubleOrNull()
                            val riskPct = riskPercentStr.toDoubleOrNull() ?: 1.0
                            val riskAmt = riskAmountStr.toDoubleOrNull() ?: 100.0
                            val pnl = pnlStr.toDoubleOrNull() ?: 0.0

                            if (pair.isBlank()) {
                                errorMessage = "Pair/Asset cannot be empty."
                                return@Button
                            }
                            if (entry == null || sl == null || tp == null) {
                                errorMessage = "Please enter valid numeric prices for Entry, SL, and TP."
                                return@Button
                            }

                            val trade = Trade(
                                id = existingTrade?.id ?: 0L,
                                dateMillis = existingTrade?.dateMillis ?: System.currentTimeMillis(),
                                dateString = dateString,
                                pair = pair.trim().uppercase(),
                                direction = direction,
                                entryPrice = entry,
                                stopLoss = sl,
                                takeProfit = tp,
                                riskPercent = riskPct,
                                riskAmount = riskAmt,
                                result = result,
                                pnl = pnl,
                                setup = setup.trim(),
                                reason = reason.trim(),
                                emotionBefore = emotionBefore.trim(),
                                emotionDuring = emotionDuring.trim(),
                                accordingToPlan = accordingToPlan,
                                fomo = fomo,
                                revenge = revenge,
                                overtrade = overtrade,
                                mistake = if (mistake.isBlank()) "None" else mistake.trim(),
                                lesson = lesson.trim(),
                                screenshotUri = screenshotUri
                            )
                            onSave(trade)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_trade_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                    ) {
                        Text("Save Trade", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun TradeDetailDialog(
    trade: Trade,
    currency: String,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = SurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderHighlight)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatusBadge(
                            text = trade.direction,
                            color = if (trade.direction == "BUY") ProfitGreen else LossRed
                        )
                        Text(
                            text = trade.pair,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                HorizontalDivider(color = BorderSubtle, modifier = Modifier.padding(vertical = 8.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // PnL Banner
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (trade.pnl >= 0) ProfitGreenBg else LossRedBg
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Result & P/L", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                                Text(
                                    text = "${trade.result} (${if (trade.pnl >= 0) "+" else ""}$currency${String.format("%.2f", trade.pnl)})",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (trade.pnl >= 0) ProfitGreen else LossRed
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Risk Amount", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                Text("$currency${trade.riskAmount} (${trade.riskPercent}%)", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                            }
                        }
                    }

                    // Price levels
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Entry", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                            Text("${trade.entryPrice}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Column {
                            Text("Stop Loss", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                            Text("${trade.stopLoss}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = LossRed)
                        }
                        Column {
                            Text("Take Profit", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                            Text("${trade.takeProfit}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = ProfitGreen)
                        }
                    }

                    HorizontalDivider(color = BorderSubtle)

                    // Setup & Reason
                    Text("Trading Setup: ${trade.setup}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = GoldAccentLight)
                    if (trade.reason.isNotBlank()) {
                        Text(trade.reason, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    }

                    // Psychology & Emotion
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCardDark),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Psychological State", style = MaterialTheme.typography.labelMedium, color = GoldAccent)
                            Text("Before Trade: ${trade.emotionBefore}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            Text("During Trade: ${trade.emotionDuring}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            Text("According to plan: ${if (trade.accordingToPlan) "Yes" else "No"}", style = MaterialTheme.typography.bodySmall, color = if (trade.accordingToPlan) ProfitGreen else LossRed)
                        }
                    }

                    // Mistakes & Lessons
                    if (trade.mistake != "None" && trade.mistake.isNotBlank()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = LossRedBg),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Mistake Identified:", style = MaterialTheme.typography.labelMedium, color = LossRedLight)
                                Text(trade.mistake, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                            }
                        }
                    }

                    if (trade.lesson.isNotBlank()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = SurfaceCardHighlight),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Lesson Learned:", style = MaterialTheme.typography.labelMedium, color = InfoBlue)
                                Text(trade.lesson, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                            }
                        }
                    }

                    // Screenshot
                    if (trade.screenshotUri != null) {
                        Text("Chart Screenshot", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                        AsyncImage(
                            model = trade.screenshotUri,
                            contentDescription = "Trade Chart",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDelete,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = LossRed)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete")
                    }
                    Button(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
