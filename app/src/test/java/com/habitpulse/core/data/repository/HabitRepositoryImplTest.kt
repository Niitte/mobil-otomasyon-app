package com.habitpulse.core.data.repository

import com.habitpulse.core.data.local.HabitDao
import com.habitpulse.core.data.local.HabitEntity
import com.habitpulse.core.data.local.HabitLogDao
import com.habitpulse.core.domain.model.FrequencyRule
import com.habitpulse.core.domain.model.Habit
import com.habitpulse.core.domain.model.HabitCategory
import com.habitpulse.core.domain.model.HabitType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class HabitRepositoryImplTest {

    @Test
    fun testHabitMapper() {
        val habit = Habit(
            id = "1",
            title = "Read",
            description = null,
            category = HabitCategory.LEARNING,
            type = HabitType.TIME_BASED,
            targetValue = 30.0,
            unit = "min",
            frequency = FrequencyRule.Weekly(3),
            reminderTime = null,
            colorHex = "#FF0000",
            isArchived = false,
            createdAt = Instant.now()
        )

        val entity = habit.toEntity()
        assertEquals("WEEKLY", entity.frequencyType)
        assertEquals("3", entity.frequencyValue)

        val mappedBack = entity.toDomain()
        assertEquals(habit.id, mappedBack.id)
        assertEquals((habit.frequency as FrequencyRule.Weekly).daysPerWeek, (mappedBack.frequency as FrequencyRule.Weekly).daysPerWeek)
    }
}
