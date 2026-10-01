package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "salah_records")
data class SalahRecord(
    @PrimaryKey val dateString: String, // format yyyy-MM-dd
    val fajr: String = "NONE", // "JAMAAT", "ALONE", "MISSED", "NONE"
    val dhuhr: String = "NONE",
    val asr: String = "NONE",
    val maghrib: String = "NONE",
    val isha: String = "NONE",
    val notes: String = ""
) {
    fun completedCount(): Int {
        val list = listOf(fajr, dhuhr, asr, maghrib, isha)
        return list.count { it == "JAMAAT" || it == "ALONE" }
    }

    fun jamaatCount(): Int {
        val list = listOf(fajr, dhuhr, asr, maghrib, isha)
        return list.count { it == "JAMAAT" }
    }

    fun missedCount(): Int {
        val list = listOf(fajr, dhuhr, asr, maghrib, isha)
        return list.count { it == "MISSED" }
    }
}
