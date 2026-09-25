package com.habitpulse.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.habitpulse.core.domain.model.HabitCategory
import com.habitpulse.core.domain.model.HabitType
import java.time.Instant
import java.time.LocalTime

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String?,
    val category: String,
    val type: String,
    val targetValue: Double,
    val unit: String?,
    val frequencyType: String, // "DAILY", "WEEKLY", "SPECIFIC_DAYS"
    val frequencyValue: String, // Value according to type, e.g. "3", "1,3,5"
    val reminderTime: String?, // Format HH:mm
    val colorHex: String,
    val isArchived: Boolean,
    val createdAt: Long // Epoch millis
)
