package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Database(
    entities = [
        Trade::class,
        SalahRecord::class,
        Habit::class,
        HabitLog::class,
        DailyReview::class,
        PsychologyLog::class,
        MonthlyGoal::class,
        UserSettings::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tradeDao(): TradeDao
    abstract fun salahDao(): SalahDao
    abstract fun habitDao(): HabitDao
    abstract fun dailyReviewDao(): DailyReviewDao
    abstract fun psychologyDao(): PsychologyDao
    abstract fun monthlyGoalDao(): MonthlyGoalDao
    abstract fun userSettingsDao(): UserSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "trader_life_database.db"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayStr = dateFormat.format(Date())
            val cal = Calendar.getInstance()

            // 1. User Settings
            database.userSettingsDao().insertOrUpdateSettings(
                UserSettings(
                    id = 1,
                    traderName = "Discipline Trader",
                    currency = "$",
                    demoBalance = 10000.0,
                    riskPerTradePct = 1.0,
                    maxDailyLoss = 200.0,
                    maxTradesPerDay = 3,
                    currentMonthTarget = 2.0,
                    language = "both"
                )
            )

            // 2. Default Habits
            val defaultHabits = listOf(
                Habit(titleEn = "Wake up on time", titleBn = "নির্দিষ্ট সময়ে ঘুম থেকে ওঠা", iconKey = "alarm", category = "Routine", orderIndex = 1),
                Habit(titleEn = "Morning Study & Market News", titleBn = "সকালের পড়াশোনা ও মার্কেট বিশ্লেষণ", iconKey = "book", category = "Trading", orderIndex = 2),
                Habit(titleEn = "Pre-Market Chart Analysis", titleBn = "প্রি-মার্কেট চার্ট বিশ্লেষণ", iconKey = "chart", category = "Trading", orderIndex = 3),
                Habit(titleEn = "Focused Trading Session", titleBn = "মনোযোগী ট্রেডিং সেশন (রুলস সহ)", iconKey = "trending_up", category = "Trading", orderIndex = 4),
                Habit(titleEn = "Physical Exercise / Workout", titleBn = "ব্যায়াম / শরীরচর্চা", iconKey = "fitness", category = "Health", orderIndex = 5),
                Habit(titleEn = "Book / Mindset Reading", titleBn = "বই বা মাইন্ডসেট পড়া", iconKey = "reading", category = "Mindset", orderIndex = 6),
                Habit(titleEn = "Family & Personal Time", titleBn = "পরিবার ও নিজের সময়", iconKey = "family", category = "Life", orderIndex = 7),
                Habit(titleEn = "Sleep on time (Discipline)", titleBn = "সময়মতো ঘুম (সুস্বাস্থ্য)", iconKey = "bedtime", category = "Routine", orderIndex = 8)
            )
            defaultHabits.forEach { database.habitDao().insertHabit(it) }

            // 3. Monthly Goals (Month 1 = $1/day, Month 2 = $2/day, etc.)
            val defaultGoals = (1..12).map { month ->
                MonthlyGoal(
                    monthNumber = month,
                    targetPerDay = month.toDouble(),
                    title = "Month $month Roadmap",
                    notes = "Target: $$month/day. Risk strictly 1% per trade. Zero revenge trades.",
                    isCompleted = false
                )
            }
            database.monthlyGoalDao().insertGoals(defaultGoals)

            // 4. Initial Salah for today
            database.salahDao().insertOrUpdateSalahRecord(
                SalahRecord(
                    dateString = todayStr,
                    fajr = "JAMAAT",
                    dhuhr = "JAMAAT",
                    asr = "NONE",
                    maghrib = "NONE",
                    isha = "NONE",
                    notes = "Stay focused on discipline and gratitude."
                )
            )

            // 5. Sample initial paper trades for realistic demonstration
            val sampleTrades = listOf(
                Trade(
                    dateMillis = System.currentTimeMillis() - 3600000 * 3,
                    dateString = todayStr,
                    pair = "EUR/USD",
                    direction = "BUY",
                    entryPrice = 1.08250,
                    stopLoss = 1.08150,
                    takeProfit = 1.08550,
                    riskPercent = 1.0,
                    riskAmount = 100.0,
                    result = "WIN",
                    pnl = 300.0,
                    setup = "Liquidity Sweep + Order Block",
                    reason = "Asian high swept, strong 15m displacement upward, entered on 5m FVG",
                    emotionBefore = "Calm",
                    emotionDuring = "Disciplined",
                    accordingToPlan = true,
                    fomo = false,
                    revenge = false,
                    overtrade = false,
                    mistake = "None",
                    lesson = "Patience paid off. Waited for confirmation instead of anticipating."
                ),
                Trade(
                    dateMillis = System.currentTimeMillis() - 3600000 * 6,
                    dateString = todayStr,
                    pair = "XAU/USD",
                    direction = "SELL",
                    entryPrice = 2655.40,
                    stopLoss = 2661.00,
                    takeProfit = 2640.00,
                    riskPercent = 1.0,
                    riskAmount = 100.0,
                    result = "LOSS",
                    pnl = -100.0,
                    setup = "Breakout Retest",
                    reason = "Trendline break retest during London open",
                    emotionBefore = "Confident",
                    emotionDuring = "Relaxed",
                    accordingToPlan = true,
                    fomo = false,
                    revenge = false,
                    overtrade = false,
                    mistake = "None",
                    lesson = "Loss is normal cost of business. SL held perfectly according to plan."
                )
            )
            sampleTrades.forEach { database.tradeDao().insertTrade(it) }

            // 6. Initial Daily Review
            database.dailyReviewDao().insertOrUpdateReview(
                DailyReview(
                    dateString = todayStr,
                    didWell = "Followed 1% risk rule on both demo trades. Did not overtrade.",
                    biggestMistake = "Slight impatience before the London session open.",
                    mistakeReason = "Checked chart too frequently before NY bell.",
                    improvement = "Set price alerts and walk away from chart until alert triggers.",
                    lesson = "Trading is 80% waiting and 20% execution.",
                    disciplineScore = 9
                )
            )

            // 7. Initial Psychology log
            database.psychologyDao().insertPsychologyLog(
                PsychologyLog(
                    dateString = todayStr,
                    timestamp = System.currentTimeMillis() - 3600000 * 4,
                    calm = 5,
                    fear = 1,
                    greed = 1,
                    fomo = 1,
                    revenge = 1,
                    confidence = 5,
                    stress = 2,
                    mindsetNote = "Pre-market check: Slept well, prayed Fajr in Jamaat, mind is calm and ready to execute only valid setups.",
                    sessionType = "Pre-Market"
                )
            )
        }
    }
}
