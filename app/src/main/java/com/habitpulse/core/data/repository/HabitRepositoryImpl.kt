package com.habitpulse.core.data.repository

import com.habitpulse.core.data.local.HabitDao
import com.habitpulse.core.data.local.HabitEntity
import com.habitpulse.core.data.local.HabitLogDao
import com.habitpulse.core.data.local.HabitLogEntity
import com.habitpulse.core.domain.model.FrequencyRule
import com.habitpulse.core.domain.model.Habit
import com.habitpulse.core.domain.model.HabitCategory
import com.habitpulse.core.domain.model.HabitLog
import com.habitpulse.core.domain.model.HabitType
import com.habitpulse.core.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class HabitRepositoryImpl(
    private val habitDao: HabitDao,
    private val habitLogDao: HabitLogDao
) : HabitRepository {

    override fun getActiveHabits(): Flow<List<Habit>> {
        return habitDao.getActiveHabits().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getHabit(id: String): Flow<Habit?> {
        return habitDao.getHabit(id).map { it?.toDomain() }
    }

    override suspend fun saveHabit(habit: Habit) {
        habitDao.insertHabit(habit.toEntity())
    }

    override suspend fun saveHabitLog(log: HabitLog) {
        habitLogDao.upsertLog(log.toEntity())
    }

    override fun getHabitLogs(habitId: String, startDate: LocalDate, endDate: LocalDate): Flow<List<HabitLog>> {
        return habitLogDao.getLogsBetweenDates(habitId, startDate.toString(), endDate.toString())
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getAllHabitLogs(habitId: String): Flow<List<HabitLog>> {
        return habitLogDao.getAllLogsForHabit(habitId)
            .map { entities -> entities.map { it.toDomain() } }
    }
}

// Mappers
fun HabitEntity.toDomain(): Habit {
    val freqType = this.frequencyType
    val freqValue = this.frequencyValue
    val rule = when (freqType) {
        "DAILY" -> FrequencyRule.Daily
        "WEEKLY" -> FrequencyRule.Weekly(freqValue.toIntOrNull() ?: 1)
        "SPECIFIC_DAYS" -> FrequencyRule.SpecificDays(
            freqValue.split(",").filter { it.isNotEmpty() }.mapNotNull { it.toIntOrNull() }.toSet()
        )
        else -> FrequencyRule.Daily
    }

    return Habit(
        id = id,
        title = title,
        description = description,
        category = HabitCategory.valueOf(category),
        type = HabitType.valueOf(type),
        targetValue = targetValue,
        unit = unit,
        frequency = rule,
        reminderTime = reminderTime?.let { LocalTime.parse(it, DateTimeFormatter.ofPattern("HH:mm")) },
        colorHex = colorHex,
        isArchived = isArchived,
        createdAt = Instant.ofEpochMilli(createdAt)
    )
}

fun Habit.toEntity(): HabitEntity {
    val (fType, fValue) = when (val f = frequency) {
        is FrequencyRule.Daily -> "DAILY" to ""
        is FrequencyRule.Weekly -> "WEEKLY" to f.daysPerWeek.toString()
        is FrequencyRule.SpecificDays -> "SPECIFIC_DAYS" to f.daysOfWeek.joinToString(",")
    }

    return HabitEntity(
        id = id,
        title = title,
        description = description,
        category = category.name,
        type = type.name,
        targetValue = targetValue,
        unit = unit,
        frequencyType = fType,
        frequencyValue = fValue,
        reminderTime = reminderTime?.format(DateTimeFormatter.ofPattern("HH:mm")),
        colorHex = colorHex,
        isArchived = isArchived,
        createdAt = createdAt.toEpochMilli()
    )
}

fun HabitLogEntity.toDomain(): HabitLog {
    return HabitLog(
        id = id,
        habitId = habitId,
        date = LocalDate.parse(date),
        value = value,
        isCompleted = isCompleted,
        notes = notes,
        loggedAt = Instant.ofEpochMilli(loggedAt)
    )
}

fun HabitLog.toEntity(): HabitLogEntity {
    return HabitLogEntity(
        id = id,
        habitId = habitId,
        date = date.toString(),
        value = value,
        isCompleted = isCompleted,
        notes = notes,
        loggedAt = loggedAt.toEpochMilli()
    )
}
