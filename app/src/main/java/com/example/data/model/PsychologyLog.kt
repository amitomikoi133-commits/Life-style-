package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "psychology_logs")
data class PsychologyLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateString: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val calm: Int = 4, // 1 - 5
    val fear: Int = 1, // 1 - 5
    val greed: Int = 1, // 1 - 5
    val fomo: Int = 1, // 1 - 5
    val revenge: Int = 1, // 1 - 5
    val confidence: Int = 4, // 1 - 5
    val stress: Int = 2, // 1 - 5
    val mindsetNote: String = "",
    val sessionType: String = "Pre-Market" // "Pre-Market", "Active-Session", "Post-Market", "Trade-Reflection"
)
