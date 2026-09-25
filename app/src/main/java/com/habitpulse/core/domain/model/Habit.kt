package com.habitpulse.core.domain.model

import java.time.Instant
import java.time.LocalTime

data class Habit(
    val id: String,
    val title: String,
    val description: String?,
    val category: HabitCategory,
    val type: HabitType,
    val targetValue: Double,
    val unit: String?,
    val frequency: FrequencyRule,
    val reminderTime: LocalTime?,
    val colorHex: String,
    val isArchived: Boolean = false,
    val createdAt: Instant
)
