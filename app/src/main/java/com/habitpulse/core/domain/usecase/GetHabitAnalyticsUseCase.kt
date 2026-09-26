package com.habitpulse.core.domain.usecase

import com.habitpulse.core.domain.analytics.StreakCalculator
import com.habitpulse.core.domain.model.FrequencyRule
import com.habitpulse.core.domain.model.HabitAnalytics
import com.habitpulse.core.domain.model.HabitLog
import com.habitpulse.core.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class GetHabitAnalyticsUseCase(
    private val repository: HabitRepository,
    private val streakCalculator: StreakCalculator
) {
    operator fun invoke(habitId: String, rule: FrequencyRule, today: LocalDate = LocalDate.now()): Flow<HabitAnalytics> {
        return repository.getAllHabitLogs(habitId).map { allLogs ->
            val (current, longest) = streakCalculator.calculate(allLogs, rule, today)

            val totalCompletions = allLogs.count { it.isCompleted }

            val oneYearAgo = today.minusDays(365)
            val logsLastYear = allLogs.filter { !it.date.isBefore(oneYearAgo) && !it.date.isAfter(today) }

            val expectedDays = when (rule) {
                is FrequencyRule.Daily -> 365
                is FrequencyRule.Weekly -> (365 / 7) * rule.daysPerWeek
                is FrequencyRule.SpecificDays -> (365 / 7) * rule.daysOfWeek.size
            }

            val completionRate = if (expectedDays > 0) {
                minOf(1f, totalCompletions.toFloat() / expectedDays.toFloat())
            } else 0f

            val heatmapData = logsLastYear.associate { log ->
                val ratio = if (log.value > 0) (log.value / 1.0).toFloat() else 0f // In actual we'd need targetValue
                log.date to ratio
            }

            HabitAnalytics(
                habitId = habitId,
                currentStreak = current,
                longestStreak = longest,
                completionRate = completionRate,
                totalCompletions = totalCompletions,
                heatmapData = heatmapData
            )
        }
    }
}
