package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_reviews")
data class DailyReview(
    @PrimaryKey val dateString: String, // yyyy-MM-dd
    val didWell: String = "",
    val biggestMistake: String = "",
    val mistakeReason: String = "",
    val improvement: String = "",
    val lesson: String = "",
    val disciplineScore: Int = 8, // 1 - 10
    val timestamp: Long = System.currentTimeMillis()
)
