package com.habitpulse.core.domain.usecase

import com.habitpulse.core.domain.analytics.StreakCalculator
import com.habitpulse.core.domain.model.FrequencyRule
import com.habitpulse.core.domain.model.Habit
import com.habitpulse.core.domain.model.HabitLog
import com.habitpulse.core.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

class GetHabitAnalyticsUseCaseTest {

    private val mockRepository = object : HabitRepository {
        override fun getActiveHabits(): Flow<List<Habit>> = flowOf(emptyList())
        override fun getHabit(id: String): Flow<Habit?> = flowOf(null)
        override suspend fun saveHabit(habit: Habit) {}
        override suspend fun saveHabitLog(log: HabitLog) {}
        override fun getHabitLogs(habitId: String, startDate: LocalDate, endDate: LocalDate): Flow<List<HabitLog>> = flowOf(emptyList())

        override fun getAllHabitLogs(habitId: String): Flow<List<HabitLog>> {
            return flowOf(
                listOf(
                    HabitLog("1", "h1", LocalDate.now().minusDays(1), 1.0, true, null, Instant.now()),
                    HabitLog("2", "h1", LocalDate.now(), 1.0, true, null, Instant.now())
                )
            )
        }
    }

    private val useCase = GetHabitAnalyticsUseCase(mockRepository, StreakCalculator())

    @Test
    fun testAnalyticsGeneration() = runTest {
        val result = useCase("h1", FrequencyRule.Daily, LocalDate.now()).first()

        assertEquals(2, result.currentStreak)
        assertEquals(2, result.totalCompletions)
        assertEquals(true, result.completionRate > 0)
        assertEquals(2, result.heatmapData.size)
    }
}
