package com.habitpulse.core.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.Instant

@RunWith(RobolectricTestRunner::class)
class HabitDaoTest {

    private lateinit var database: HabitDatabase
    private lateinit var habitDao: HabitDao
    private lateinit var habitLogDao: HabitLogDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, HabitDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        habitDao = database.habitDao()
        habitLogDao = database.habitLogDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetHabit() = runTest {
        val habit = HabitEntity(
            id = "1",
            title = "Drink Water",
            description = null,
            category = "HEALTH",
            type = "BOOLEAN",
            targetValue = 1.0,
            unit = "ml",
            frequencyType = "DAILY",
            frequencyValue = "",
            reminderTime = null,
            colorHex = "#0000FF",
            isArchived = false,
            createdAt = Instant.now().toEpochMilli()
        )

        habitDao.insertHabit(habit)
        val result = habitDao.getHabitSync("1")
        assertEquals("Drink Water", result?.title)
    }
}
