package com.habitpulse.core.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate

@Entity(
    tableName = "habit_logs",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("habitId")]
)
data class HabitLogEntity(
    @PrimaryKey val id: String,
    val habitId: String,
    val date: String, // ISO format YYYY-MM-DD
    val value: Double,
    val isCompleted: Boolean,
    val notes: String?,
    val loggedAt: Long // Epoch millis
)
