package com.habitpulse.core.domain.model

import java.time.Instant
import java.time.LocalDate

data class HabitLog(
    val id: String,
    val habitId: String,
    val date: LocalDate,
    val value: Double,
    val isCompleted: Boolean,
    val notes: String?,
    val loggedAt: Instant
)
