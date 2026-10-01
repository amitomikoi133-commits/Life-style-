package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.TraderLifeRepository
import com.example.util.DateUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppScreen(val title: String, val bnTitle: String) {
    DASHBOARD("Dashboard", "ড্যাশবোর্ড"),
    JOURNAL("Trading Journal", "ট্রেডিং জার্নাল"),
    RISK_MANAGER("Risk Manager", "রিস্ক ম্যানেজার"),
    PSYCHOLOGY("Psychology", "সাইকোলজি"),
    ROUTINE("Daily Routine", "দৈনিক রুটিন"),
    SALAH("Salah Tracker", "সালাত ট্র্যাকার"),
    GOALS("Goals Roadmap", "রোডম্যাপ ও লক্ষ্য"),
    ANALYTICS("Analytics", "অ্যানালিটিক্স"),
    DAILY_REVIEW("Daily Review", "দৈনিক পর্যালোচনা"),
    SETTINGS("Settings", "সেটিংস")
}

data class DashboardStats(
    val dailyTarget: Double = 2.0,
    val todayPnl: Double = 0.0,
    val todayTradesCount: Int = 0,
    val todayWins: Int = 0,
    val todayLosses: Int = 0,
    val todayBreakeven: Int = 0,
    val winRate: Double = 0.0,
    val targetProgressPct: Float = 0f,
    val ruleViolationsCount: Int = 0,
    val isOvertrading: Boolean = false,
    val maxTradesAllowed: Int = 3,
    val riskStatus: String = "SAFE", // "SAFE", "CAUTION", "BREACHED"
    val todayRisked: Double = 0.0,
    val maxDailyLossAllowed: Double = 200.0,
    val disciplineScore: Int = 100, // 0 - 100
    val salahCompletedCount: Int = 0, // 0 - 5
    val salahJamaatCount: Int = 0,
    val routineCompletedCount: Int = 0,
    val routineTotalCount: Int = 0,
    val routineCompletionPct: Float = 0f,
    val currentMonthTargetPerDay: Double = 2.0,
    val currentMonthAvgDailyPnl: Double = 0.0,
    val currentMonthTotalPnl: Double = 0.0
)

class TraderLifeViewModel(
    private val repository: TraderLifeRepository
) : ViewModel() {

    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _selectedDate = MutableStateFlow(DateUtils.getTodayString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    // Search and Filters for Journal
    val journalSearchQuery = MutableStateFlow("")
    val journalFilterResult = MutableStateFlow("ALL") // "ALL", "WIN", "LOSS", "BREAKEVEN", "OPEN"
    val journalFilterDirection = MutableStateFlow("ALL") // "ALL", "BUY", "SELL"
    val journalFilterMistakeOnly = MutableStateFlow(false)

    // Data streams from repository
    val allTrades = repository.allTrades.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allSalah = repository.allSalahRecords.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val activeHabits = repository.activeHabits.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allHabits = repository.allHabits.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allHabitLogs = repository.allHabitLogs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allReviews = repository.allReviews.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allPsychologyLogs = repository.allPsychologyLogs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allGoals = repository.allGoals.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val userSettings = repository.settingsFlow.map { it ?: UserSettings() }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())

    init {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val existing = repository.getSettingsDirect()
            if (existing == null) {
                val todayStr = DateUtils.getTodayString()
                repository.saveSettings(
                    UserSettings(
                        id = 1,
                        traderName = "Disciplined Trader",
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
                defaultHabits.forEach { repository.insertHabit(it) }

                // 3. Monthly Goals (Month 1 = $1/day, Month 2 = $2/day, etc.)
                (1..12).forEach { month ->
                    repository.saveGoal(
                        MonthlyGoal(
                            monthNumber = month,
                            targetPerDay = month.toDouble(),
                            title = "Month $month Roadmap",
                            notes = "Target: $$month/day. Risk strictly 1% per trade. Zero revenge trades."
                        )
                    )
                }

                // 4. Initial Salah for today
                repository.saveSalahRecord(
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
                repository.insertTrade(
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
                    )
                )
                repository.insertTrade(
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

                // 6. Initial Daily Review
                repository.saveReview(
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
                repository.insertPsychologyLog(
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

    // Feedback notifications
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    fun setScreen(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    // Calculated Dashboard Stats
    val dashboardStats: StateFlow<DashboardStats> = combine(
        allTrades,
        allSalah,
        activeHabits,
        allHabitLogs,
        userSettings,
        _selectedDate
    ) { args: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        val trades = args[0] as List<Trade>
        @Suppress("UNCHECKED_CAST")
        val salahList = args[1] as List<SalahRecord>
        @Suppress("UNCHECKED_CAST")
        val habits = args[2] as List<Habit>
        @Suppress("UNCHECKED_CAST")
        val habitLogs = args[3] as List<HabitLog>
        val settings = args[4] as UserSettings
        val selDate = args[5] as String
        val todayTrades = trades.filter { it.dateString == selDate }
        val todayPnl = todayTrades.sumOf { it.pnl }
        val wins = todayTrades.count { it.result == "WIN" }
        val losses = todayTrades.count { it.result == "LOSS" }
        val bes = todayTrades.count { it.result == "BREAKEVEN" }
        val winRate = if (wins + losses > 0) (wins.toDouble() / (wins + losses)) * 100.0 else 0.0

        val target = settings.currentMonthTarget
        val targetProgress = if (target > 0) ((todayPnl / target).toFloat()).coerceIn(0f, 2f) else 0f

        val ruleViolations = todayTrades.count { !it.accordingToPlan || it.fomo || it.revenge || it.overtrade }
        val isOvertrading = todayTrades.size > settings.maxTradesPerDay

        val todayRisked = todayTrades.sumOf { it.riskAmount }
        val riskStatus = when {
            todayTrades.any { it.pnl <= -settings.maxDailyLoss } -> "BREACHED"
            todayPnl <= -settings.maxDailyLoss * 0.7 -> "CAUTION"
            todayRisked > (settings.demoBalance * (settings.riskPerTradePct / 100.0) * settings.maxTradesPerDay) -> "CAUTION"
            else -> "SAFE"
        }

        val todaySalah = salahList.find { it.dateString == selDate } ?: SalahRecord(dateString = selDate)
        val salahCount = todaySalah.completedCount()
        val jamaatCount = todaySalah.jamaatCount()

        val todayLogs = habitLogs.filter { it.dateString == selDate && it.isCompleted }
        val routineDone = todayLogs.size
        val routineTotal = habits.size
        val routinePct = if (routineTotal > 0) (routineDone.toFloat() / routineTotal) else 0f

        // Discipline Score Calculation (100 base)
        var discipline = 100
        if (isOvertrading) discipline -= 20
        ruleViolations.let { discipline -= (it * 15) }
        if (todayTrades.any { it.fomo }) discipline -= 15
        if (todayTrades.any { it.revenge }) discipline -= 20
        if (riskStatus == "BREACHED") discipline -= 25
        if (routineTotal > 0 && routinePct < 0.5f) discipline -= 10
        if (salahCount < 3) discipline -= 10
        discipline = discipline.coerceIn(0, 100)

        // Month stats
        val totalMonthPnl = trades.sumOf { it.pnl }
        val daysInMonth = DateUtils.getDaysInCurrentMonth()
        val avgDailyPnl = if (daysInMonth > 0) totalMonthPnl / daysInMonth else 0.0

        DashboardStats(
            dailyTarget = target,
            todayPnl = todayPnl,
            todayTradesCount = todayTrades.size,
            todayWins = wins,
            todayLosses = losses,
            todayBreakeven = bes,
            winRate = winRate,
            targetProgressPct = targetProgress,
            ruleViolationsCount = ruleViolations,
            isOvertrading = isOvertrading,
            maxTradesAllowed = settings.maxTradesPerDay,
            riskStatus = riskStatus,
            todayRisked = todayRisked,
            maxDailyLossAllowed = settings.maxDailyLoss,
            disciplineScore = discipline,
            salahCompletedCount = salahCount,
            salahJamaatCount = jamaatCount,
            routineCompletedCount = routineDone,
            routineTotalCount = routineTotal,
            routineCompletionPct = routinePct,
            currentMonthTargetPerDay = settings.currentMonthTarget,
            currentMonthAvgDailyPnl = avgDailyPnl,
            currentMonthTotalPnl = totalMonthPnl
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardStats())

    // Filtered Trades list for Journal
    val filteredTrades = combine(
        allTrades,
        journalSearchQuery,
        journalFilterResult,
        journalFilterDirection,
        journalFilterMistakeOnly
    ) { list, query, resFilter, dirFilter, mistakeOnly ->
        list.filter { trade ->
            val matchesQuery = query.isBlank() ||
                    trade.pair.contains(query, ignoreCase = true) ||
                    trade.setup.contains(query, ignoreCase = true) ||
                    trade.reason.contains(query, ignoreCase = true) ||
                    trade.mistake.contains(query, ignoreCase = true) ||
                    trade.lesson.contains(query, ignoreCase = true)

            val matchesResult = resFilter == "ALL" || trade.result.equals(resFilter, ignoreCase = true)
            val matchesDir = dirFilter == "ALL" || trade.direction.equals(dirFilter, ignoreCase = true)
            val matchesMistake = !mistakeOnly || (trade.mistake != "None" && trade.mistake.isNotBlank()) || trade.fomo || trade.revenge || trade.overtrade || !trade.accordingToPlan

            matchesQuery && matchesResult && matchesDir && matchesMistake
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Trade operations
    fun saveTrade(trade: Trade) {
        viewModelScope.launch {
            if (trade.id == 0L) {
                repository.insertTrade(trade)
                showMessage("Trade logged successfully! / ট্রেড সংরক্ষণ সম্পন্ন হয়েছে")
            } else {
                repository.updateTrade(trade)
                showMessage("Trade updated successfully! / ট্রেড আপডেট হয়েছে")
            }
        }
    }

    fun deleteTrade(trade: Trade) {
        viewModelScope.launch {
            repository.deleteTrade(trade)
            showMessage("Trade removed / ট্রেড মুছে ফেলা হয়েছে")
        }
    }

    // Salah operations
    fun updateSalah(prayer: String, status: String, date: String = _selectedDate.value) {
        viewModelScope.launch {
            val existing = repository.getSalahRecordDirect(date) ?: SalahRecord(dateString = date)
            val updated = when (prayer.lowercase()) {
                "fajr" -> existing.copy(fajr = status)
                "dhuhr" -> existing.copy(dhuhr = status)
                "asr" -> existing.copy(asr = status)
                "maghrib" -> existing.copy(maghrib = status)
                "isha" -> existing.copy(isha = status)
                else -> existing
            }
            repository.saveSalahRecord(updated)
            showMessage("$prayer status: $status")
        }
    }

    // Habit operations
    fun toggleHabit(habitId: Long, isCompleted: Boolean, date: String = _selectedDate.value) {
        viewModelScope.launch {
            repository.toggleHabitLog(habitId, date, isCompleted)
        }
    }

    fun addCustomHabit(titleEn: String, titleBn: String, iconKey: String, category: String) {
        viewModelScope.launch {
            val habit = Habit(
                titleEn = titleEn,
                titleBn = if (titleBn.isBlank()) titleEn else titleBn,
                iconKey = iconKey,
                category = category,
                orderIndex = 99
            )
            repository.insertHabit(habit)
            showMessage("New habit added! / নতুন অভ্যাস যোগ করা হয়েছে")
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
            showMessage("Habit deleted / অভ্যাস মোছা হয়েছে")
        }
    }

    // Daily Review operations
    fun saveDailyReview(review: DailyReview) {
        viewModelScope.launch {
            repository.saveReview(review)
            showMessage("Daily review saved! / আজকের পর্যালোচনা সংরক্ষিত হয়েছে")
        }
    }

    // Psychology operations
    fun savePsychologyLog(log: PsychologyLog) {
        viewModelScope.launch {
            repository.insertPsychologyLog(log)
            showMessage("Mindset & Psychology log recorded!")
        }
    }

    // Goals operations
    fun updateMonthlyGoal(goal: MonthlyGoal) {
        viewModelScope.launch {
            repository.updateGoal(goal)
            showMessage("Goal target updated!")
        }
    }

    // User Settings operations
    fun updateSettings(settings: UserSettings) {
        viewModelScope.launch {
            repository.saveSettings(settings)
            showMessage("Settings updated successfully! / সেটিংস সংরক্ষিত হয়েছে")
        }
    }
}

class TraderLifeViewModelFactory(
    private val repository: TraderLifeRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TraderLifeViewModel::class.java)) {
            return TraderLifeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
