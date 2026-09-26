package com.habitpulse.core.domain.model

import java.time.LocalDate

data class HabitAnalytics(
    val habitId: String,
    val currentStreak: Int,
    val longestStreak: Int,
    val completionRate: Float,
    val totalCompletions: Int,
    val heatmapData: Map<LocalDate, Float>
)
