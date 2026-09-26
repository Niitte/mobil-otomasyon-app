package com.habitpulse.core.domain.analytics

import com.habitpulse.core.domain.model.FrequencyRule
import com.habitpulse.core.domain.model.HabitLog
import java.time.LocalDate

class StreakCalculator {

    fun calculate(logs: List<HabitLog>, rule: FrequencyRule, today: LocalDate = LocalDate.now()): Pair<Int, Int> {
        if (logs.isEmpty()) return 0 to 0

        val completedDates = logs.filter { it.isCompleted }.map { it.date }.toSet()
        if (completedDates.isEmpty()) return 0 to 0

        var currentStreak = 0
        var longestStreak = 0
        var tempStreak = 0

        val sortedDates = completedDates.sorted()
        val firstDate = sortedDates.first()
        val lastDate = today

        when (rule) {
            is FrequencyRule.Daily -> {
                var currentDate = firstDate
                while (!currentDate.isAfter(lastDate)) {
                    if (completedDates.contains(currentDate)) {
                        tempStreak++
                        longestStreak = maxOf(longestStreak, tempStreak)
                    } else {
                        // Skip if it is today and hasn't been completed yet
                        if (currentDate != today) {
                            tempStreak = 0
                        }
                    }
                    currentDate = currentDate.plusDays(1)
                }
                currentStreak = tempStreak
            }
            is FrequencyRule.Weekly -> {
                // A simplified week streak logic:
                // Count successful weeks.
                val weeks = completedDates.groupBy { it.year to (it.dayOfYear / 7) }
                var currentWDate = firstDate
                while (!currentWDate.isAfter(lastDate)) {
                    val weekKey = currentWDate.year to (currentWDate.dayOfYear / 7)
                    val daysCompletedInWeek = weeks[weekKey]?.size ?: 0
                    if (daysCompletedInWeek >= rule.daysPerWeek) {
                        tempStreak++
                        longestStreak = maxOf(longestStreak, tempStreak)
                    } else {
                        val currentWeekKey = today.year to (today.dayOfYear / 7)
                        if (weekKey != currentWeekKey) {
                            tempStreak = 0
                        }
                    }
                    currentWDate = currentWDate.plusDays(7)
                }
                currentStreak = tempStreak
            }
            is FrequencyRule.SpecificDays -> {
                 var currentDate = firstDate
                 while (!currentDate.isAfter(lastDate)) {
                     val dayOfWeek = currentDate.dayOfWeek.value
                     if (rule.daysOfWeek.contains(dayOfWeek)) {
                         if (completedDates.contains(currentDate)) {
                             tempStreak++
                             longestStreak = maxOf(longestStreak, tempStreak)
                         } else {
                             if (currentDate != today) {
                                 tempStreak = 0
                             }
                         }
                     }
                     currentDate = currentDate.plusDays(1)
                 }
                 currentStreak = tempStreak
            }
        }

        return currentStreak to longestStreak
    }
}
