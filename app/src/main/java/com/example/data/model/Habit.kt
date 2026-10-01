package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titleEn: String,
    val titleBn: String,
    val iconKey: String = "alarm", // "alarm", "book", "chart", "trending_up", "fitness", "reading", "family", "bedtime"
    val category: String = "Trading",
    val isActive: Boolean = true,
    val orderIndex: Int = 0
)

@Entity(tableName = "habit_logs")
data class HabitLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val dateString: String, // yyyy-MM-dd
    val isCompleted: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
