package com.habitpulse.core.domain.analytics

import com.habitpulse.core.domain.model.FrequencyRule
import com.habitpulse.core.domain.model.HabitLog
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

class StreakCalculatorTest {

    private val calculator = StreakCalculator()

    @Test
    fun testEmptyLogs() {
        val (current, longest) = calculator.calculate(emptyList(), FrequencyRule.Daily)
        assertEquals(0, current)
        assertEquals(0, longest)
    }

    @Test
    fun testDailyStreak_consecutive() {
        val today = LocalDate.of(2023, 10, 5)
        val logs = listOf(
            HabitLog("1", "h1", LocalDate.of(2023, 10, 1), 1.0, true, null, Instant.now()),
            HabitLog("2", "h1", LocalDate.of(2023, 10, 2), 1.0, true, null, Instant.now()),
            HabitLog("3", "h1", LocalDate.of(2023, 10, 3), 1.0, true, null, Instant.now()),
            HabitLog("4", "h1", LocalDate.of(2023, 10, 4), 1.0, true, null, Instant.now()),
        )
        val (current, longest) = calculator.calculate(logs, FrequencyRule.Daily, today)
        assertEquals(4, current)
        assertEquals(4, longest)
    }

    @Test
    fun testDailyStreak_broken() {
        val today = LocalDate.of(2023, 10, 5)
        val logs = listOf(
            HabitLog("1", "h1", LocalDate.of(2023, 10, 1), 1.0, true, null, Instant.now()),
            HabitLog("2", "h1", LocalDate.of(2023, 10, 2), 1.0, true, null, Instant.now()),
            // missed 3rd
            HabitLog("4", "h1", LocalDate.of(2023, 10, 4), 1.0, true, null, Instant.now()),
        )
        val (current, longest) = calculator.calculate(logs, FrequencyRule.Daily, today)
        assertEquals(1, current)
        assertEquals(2, longest)
    }
}
