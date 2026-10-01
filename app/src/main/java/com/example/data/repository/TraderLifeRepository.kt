package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class TraderLifeRepository(
    private val tradeDao: TradeDao,
    private val salahDao: SalahDao,
    private val habitDao: HabitDao,
    private val dailyReviewDao: DailyReviewDao,
    private val psychologyDao: PsychologyDao,
    private val monthlyGoalDao: MonthlyGoalDao,
    private val userSettingsDao: UserSettingsDao
) {
    // Trades
    val allTrades: Flow<List<Trade>> = tradeDao.getAllTrades()
    fun getTradesByDate(date: String): Flow<List<Trade>> = tradeDao.getTradesByDate(date)
    suspend fun insertTrade(trade: Trade): Long = tradeDao.insertTrade(trade)
    suspend fun updateTrade(trade: Trade) = tradeDao.updateTrade(trade)
    suspend fun deleteTrade(trade: Trade) = tradeDao.deleteTrade(trade)
    suspend fun deleteTradeById(id: Long) = tradeDao.deleteTradeById(id)
    suspend fun deleteAllTrades() = tradeDao.deleteAllTrades()

    // Salah
    val allSalahRecords: Flow<List<SalahRecord>> = salahDao.getAllSalahRecords()
    fun getSalahRecordByDate(date: String): Flow<SalahRecord?> = salahDao.getSalahRecordByDate(date)
    suspend fun getSalahRecordDirect(date: String): SalahRecord? = salahDao.getSalahRecordDirect(date)
    suspend fun saveSalahRecord(record: SalahRecord) = salahDao.insertOrUpdateSalahRecord(record)

    // Habits
    val activeHabits: Flow<List<Habit>> = habitDao.getAllActiveHabits()
    val allHabits: Flow<List<Habit>> = habitDao.getAllHabits()
    val allHabitLogs: Flow<List<HabitLog>> = habitDao.getAllHabitLogs()
    fun getHabitLogsByDate(date: String): Flow<List<HabitLog>> = habitDao.getHabitLogsByDate(date)
    suspend fun insertHabit(habit: Habit): Long = habitDao.insertHabit(habit)
    suspend fun updateHabit(habit: Habit) = habitDao.updateHabit(habit)
    suspend fun deleteHabit(habit: Habit) {
        habitDao.deleteLogsForHabit(habit.id)
        habitDao.deleteHabit(habit)
    }
    suspend fun toggleHabitLog(habitId: Long, dateString: String, isCompleted: Boolean) {
        if (isCompleted) {
            habitDao.insertHabitLog(HabitLog(habitId = habitId, dateString = dateString, isCompleted = true))
        } else {
            habitDao.deleteHabitLog(habitId, dateString)
        }
    }

    // Daily Review
    val allReviews: Flow<List<DailyReview>> = dailyReviewDao.getAllReviews()
    fun getReviewByDate(date: String): Flow<DailyReview?> = dailyReviewDao.getReviewByDate(date)
    suspend fun saveReview(review: DailyReview) = dailyReviewDao.insertOrUpdateReview(review)
    suspend fun deleteReview(review: DailyReview) = dailyReviewDao.deleteReview(review)

    // Psychology
    val allPsychologyLogs: Flow<List<PsychologyLog>> = psychologyDao.getAllPsychologyLogs()
    fun getPsychologyLogsByDate(date: String): Flow<List<PsychologyLog>> = psychologyDao.getPsychologyLogsByDate(date)
    suspend fun insertPsychologyLog(log: PsychologyLog): Long = psychologyDao.insertPsychologyLog(log)
    suspend fun deletePsychologyLog(log: PsychologyLog) = psychologyDao.deletePsychologyLog(log)

    // Monthly Goals
    val allGoals: Flow<List<MonthlyGoal>> = monthlyGoalDao.getAllGoals()
    suspend fun saveGoal(goal: MonthlyGoal) = monthlyGoalDao.insertOrUpdateGoal(goal)
    suspend fun updateGoal(goal: MonthlyGoal) = monthlyGoalDao.updateGoal(goal)

    // Settings
    val settingsFlow: Flow<UserSettings?> = userSettingsDao.getSettingsFlow()
    suspend fun getSettingsDirect(): UserSettings? = userSettingsDao.getSettingsDirect()
    suspend fun saveSettings(settings: UserSettings) = userSettingsDao.insertOrUpdateSettings(settings)
}
