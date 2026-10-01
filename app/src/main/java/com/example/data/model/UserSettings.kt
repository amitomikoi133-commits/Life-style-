package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monthly_goals")
data class MonthlyGoal(
    @PrimaryKey val monthNumber: Int, // 1 to 12
    val targetPerDay: Double, // e.g. 1.0, 2.0, 3.0, 4.0, ...
    val title: String = "Month $monthNumber Target",
    val notes: String = "Discipline & consistency focus",
    val isCompleted: Boolean = false
)

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey val id: Int = 1,
    val traderName: String = "Master Trader",
    val currency: String = "$",
    val demoBalance: Double = 10000.0,
    val riskPerTradePct: Double = 1.0,
    val maxDailyLoss: Double = 200.0,
    val maxTradesPerDay: Int = 3,
    val currentMonthTarget: Double = 2.0, // $/day
    val language: String = "both", // "en", "bn", "both"
    val themeVariant: String = "dark_gold",
    val pinCode: String = "",
    val isPinLocked: Boolean = false
)
