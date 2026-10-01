package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.SalahRecord
import com.example.ui.components.GlowCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.TraderLifeViewModel
import com.example.util.DateUtils

@Composable
fun SalahScreen(
    viewModel: TraderLifeViewModel,
    modifier: Modifier = Modifier
) {
    val allSalah by viewModel.allSalah.collectAsStateWithLifecycle()
    val settings by viewModel.userSettings.collectAsStateWithLifecycle()
    val today = DateUtils.getTodayString()

    val todayRecord = allSalah.find { it.dateString == today } ?: SalahRecord(dateString = today)
    val completedCount = todayRecord.completedCount()
    val jamaatCount = todayRecord.jamaatCount()

    // Monthly stats
    val totalLoggedDays = allSalah.size.coerceAtLeast(1)
    val totalJamaatAllTime = allSalah.sumOf { it.jamaatCount() }
    val totalAloneAllTime = allSalah.sumOf { it.completedCount() - it.jamaatCount() }
    val totalMissedAllTime = allSalah.sumOf { it.missedCount() }
    val totalPrayersPossible = totalLoggedDays * 5
    val jamaatPct = ((totalJamaatAllTime.toDouble() / totalPrayersPossible) * 100).toInt()
    val alonePct = ((totalAloneAllTime.toDouble() / totalPrayersPossible) * 100).toInt()
    val missedPct = ((totalMissedAllTime.toDouble() / totalPrayersPossible) * 100).toInt()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
    ) {
        // Islamic Discipline Reminder Card
        item {
            GlowCard(borderColor = GoldAccent.copy(alpha = 0.4f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (settings.language == "bn") "সালাত ও আত্মিক শৃঙ্খলা" else "Salah & Spiritual Discipline",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccentLight
                        )
                        Text(
                            text = "“Indeed, prayer prohibits immorality and wrongdoing.” (29:45)",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(GoldAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Mosque, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's Status: $completedCount/5 Prayed ($jamaatCount in Jamaat)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    StatusBadge(
                        text = if (completedCount == 5) "PERFECT 5/5" else "$completedCount/5",
                        color = if (completedCount == 5) ProfitGreen else GoldAccent
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { (completedCount / 5f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (completedCount == 5) ProfitGreen else GoldAccent,
                    trackColor = SurfaceCardHighlight
                )
            }
        }

        // 5 Daily Prayers
        val prayers = listOf(
            PrayerInfo("fajr", "Fajr (ফজর)", "Dawn prayer before sunrise", todayRecord.fajr, SalahFajr),
            PrayerInfo("dhuhr", "Dhuhr (যোহর)", "Noon prayer after sun peak", todayRecord.dhuhr, SalahDhuhr),
            PrayerInfo("asr", "Asr (আসর)", "Afternoon prayer before sunset", todayRecord.asr, SalahAsr),
            PrayerInfo("maghrib", "Maghrib (মাগরিব)", "Sunset prayer", todayRecord.maghrib, SalahMaghrib),
            PrayerInfo("isha", "Isha (এশা)", "Night prayer", todayRecord.isha, SalahIsha)
        )

        items(prayers, key = { it.key }) { prayer ->
            SalahPrayerCard(
                prayerInfo = prayer,
                onStatusChange = { newStatus ->
                    viewModel.updateSalah(prayer.key, newStatus)
                }
            )
        }

        // Monthly Statistics Card
        item {
            GlowCard(borderColor = BorderSubtle) {
                Text(
                    text = "Monthly Salah Statistics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Historical consistency across recorded days.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$jamaatPct%", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = ProfitGreen)
                        Text("In Jamaat", style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$alonePct%", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = GoldAccentLight)
                        Text("Alone (একাকী)", style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$missedPct%", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = LossRed)
                        Text("Missed (কাজা)", style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

data class PrayerInfo(
    val key: String,
    val name: String,
    val description: String,
    val status: String,
    val accentColor: Color
)

@Composable
fun SalahPrayerCard(
    prayerInfo: PrayerInfo,
    onStatusChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                when (prayerInfo.status) {
                    "JAMAAT" -> ProfitGreen.copy(alpha = 0.5f)
                    "ALONE" -> GoldAccent.copy(alpha = 0.5f)
                    "MISSED" -> LossRed.copy(alpha = 0.5f)
                    else -> BorderSubtle
                },
                RoundedCornerShape(14.dp)
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCardDark)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(prayerInfo.accentColor)
                    )
                    Text(
                        text = prayerInfo.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                StatusBadge(
                    text = when (prayerInfo.status) {
                        "JAMAAT" -> "JAMAAT (জামাত)"
                        "ALONE" -> "ALONE (একাকী)"
                        "MISSED" -> "MISSED (কাজা)"
                        else -> "PENDING"
                    },
                    color = when (prayerInfo.status) {
                        "JAMAAT" -> ProfitGreen
                        "ALONE" -> GoldAccent
                        "MISSED" -> LossRed
                        else -> TextTertiary
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3 Action Buttons: Jamaat, Alone, Missed
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = { onStatusChange("JAMAAT") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (prayerInfo.status == "JAMAAT") ProfitGreen else SurfaceDark
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Jamaat",
                        color = if (prayerInfo.status == "JAMAAT") Color.Black else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { onStatusChange("ALONE") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (prayerInfo.status == "ALONE") GoldAccent else SurfaceDark
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Alone",
                        color = if (prayerInfo.status == "ALONE") Color.Black else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { onStatusChange("MISSED") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (prayerInfo.status == "MISSED") LossRed else SurfaceDark
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Missed",
                        color = if (prayerInfo.status == "MISSED") Color.White else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
