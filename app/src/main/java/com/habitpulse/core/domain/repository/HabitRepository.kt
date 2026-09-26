package com.habitpulse.core.domain.repository

import com.habitpulse.core.domain.model.Habit
import com.habitpulse.core.domain.model.HabitLog
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface HabitRepository {
    fun getActiveHabits(): Flow<List<Habit>>
    fun getHabit(id: String): Flow<Habit?>
    suspend fun saveHabit(habit: Habit)
    suspend fun saveHabitLog(log: HabitLog)
    fun getHabitLogs(habitId: String, startDate: LocalDate, endDate: LocalDate): Flow<List<HabitLog>>
    fun getAllHabitLogs(habitId: String): Flow<List<HabitLog>>
}
