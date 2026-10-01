package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trades")
data class Trade(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateMillis: Long = System.currentTimeMillis(),
    val dateString: String = "",
    val pair: String = "EUR/USD",
    val direction: String = "BUY", // "BUY", "SELL"
    val entryPrice: Double = 0.0,
    val stopLoss: Double = 0.0,
    val takeProfit: Double = 0.0,
    val riskPercent: Double = 1.0,
    val riskAmount: Double = 100.0,
    val result: String = "WIN", // "WIN", "LOSS", "BREAKEVEN", "OPEN"
    val pnl: Double = 0.0,
    val setup: String = "Breakout",
    val reason: String = "",
    val emotionBefore: String = "Calm",
    val emotionDuring: String = "Disciplined",
    val accordingToPlan: Boolean = true,
    val fomo: Boolean = false,
    val revenge: Boolean = false,
    val overtrade: Boolean = false,
    val mistake: String = "None",
    val lesson: String = "",
    val screenshotUri: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
