package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.model.SalahRecord
import com.example.data.model.Trade
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Trader Life", appName)
    }

    @Test
    fun `test trade model and pnl calculation`() {
        val winTrade = Trade(
            pair = "EUR/USD",
            direction = "BUY",
            entryPrice = 1.08200,
            stopLoss = 1.08000,
            takeProfit = 1.08600,
            riskAmount = 100.0,
            result = "WIN",
            pnl = 200.0
        )
        assertEquals(200.0, winTrade.pnl, 0.001)
        assertEquals("WIN", winTrade.result)
    }

    @Test
    fun `test salah model completion count`() {
        val record = SalahRecord(
            dateString = "2026-10-01",
            fajr = "JAMAAT",
            dhuhr = "JAMAAT",
            asr = "ALONE",
            maghrib = "MISSED",
            isha = "NONE"
        )
        assertEquals(3, record.completedCount())
        assertEquals(2, record.jamaatCount())
        assertEquals(1, record.missedCount())
    }
}
