package com.example.data.local

import androidx.room.*
import com.example.data.model.DailyReview
import com.example.data.model.Habit
import com.example.data.model.HabitLog
import com.example.data.model.MonthlyGoal
import com.example.data.model.PsychologyLog
import com.example.data.model.SalahRecord
import com.example.data.model.UserSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface SalahDao {
    @Query("SELECT * FROM salah_records ORDER BY dateString DESC")
    fun getAllSalahRecords(): Flow<List<SalahRecord>>

    @Query("SELECT * FROM salah_records WHERE dateString = :dateString")
    fun getSalahRecordByDate(dateString: String): Flow<SalahRecord?>

    @Query("SELECT * FROM salah_records WHERE dateString = :dateString")
    suspend fun getSalahRecordDirect(dateString: String): SalahRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSalahRecord(record: SalahRecord)

    @Query("DELETE FROM salah_records")
    suspend fun deleteAllSalah()
}

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits WHERE isActive = 1 ORDER BY orderIndex ASC, id ASC")
    fun getAllActiveHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM habits ORDER BY orderIndex ASC, id ASC")
    fun getAllHabits(): Flow<List<Habit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit): Long

    @Update
    suspend fun updateHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Query("SELECT * FROM habit_logs WHERE dateString = :dateString")
    fun getHabitLogsByDate(dateString: String): Flow<List<HabitLog>>

    @Query("SELECT * FROM habit_logs")
    fun getAllHabitLogs(): Flow<List<HabitLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitLog(log: HabitLog)

    @Query("DELETE FROM habit_logs WHERE habitId = :habitId AND dateString = :dateString")
    suspend fun deleteHabitLog(habitId: Long, dateString: String)

    @Query("DELETE FROM habit_logs WHERE habitId = :habitId")
    suspend fun deleteLogsForHabit(habitId: Long)
}

@Dao
interface DailyReviewDao {
    @Query("SELECT * FROM daily_reviews ORDER BY dateString DESC")
    fun getAllReviews(): Flow<List<DailyReview>>

    @Query("SELECT * FROM daily_reviews WHERE dateString = :dateString")
    fun getReviewByDate(dateString: String): Flow<DailyReview?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateReview(review: DailyReview)

    @Delete
    suspend fun deleteReview(review: DailyReview)
}

@Dao
interface PsychologyDao {
    @Query("SELECT * FROM psychology_logs ORDER BY timestamp DESC")
    fun getAllPsychologyLogs(): Flow<List<PsychologyLog>>

    @Query("SELECT * FROM psychology_logs WHERE dateString = :dateString ORDER BY timestamp DESC")
    fun getPsychologyLogsByDate(dateString: String): Flow<List<PsychologyLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPsychologyLog(log: PsychologyLog): Long

    @Delete
    suspend fun deletePsychologyLog(log: PsychologyLog)
}

@Dao
interface MonthlyGoalDao {
    @Query("SELECT * FROM monthly_goals ORDER BY monthNumber ASC")
    fun getAllGoals(): Flow<List<MonthlyGoal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoals(goals: List<MonthlyGoal>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateGoal(goal: MonthlyGoal)

    @Update
    suspend fun updateGoal(goal: MonthlyGoal)
}

@Dao
interface UserSettingsDao {
    @Query("SELECT * FROM user_settings WHERE id = 1")
    fun getSettingsFlow(): Flow<UserSettings?>

    @Query("SELECT * FROM user_settings WHERE id = 1")
    suspend fun getSettingsDirect(): UserSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSettings(settings: UserSettings)
}
