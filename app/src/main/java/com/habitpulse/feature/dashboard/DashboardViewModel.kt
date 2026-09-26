package com.habitpulse.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitpulse.core.domain.model.Habit
import com.habitpulse.core.domain.model.HabitLog
import com.habitpulse.core.domain.repository.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

class DashboardViewModel(
    private val repository: HabitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadHabits()
    }

    private fun loadHabits() {
        viewModelScope.launch {
            repository.getActiveHabits()
                .catch { e -> _uiState.value = DashboardUiState.Error(e.message ?: "Unknown error") }
                .collect { habits ->
                    if (habits.isEmpty()) {
                        _uiState.value = DashboardUiState.Empty
                    } else {
                        _uiState.value = DashboardUiState.Success(habits)
                    }
                }
        }
    }

    fun onHabitToggled(habit: Habit, isCompleted: Boolean) {
        viewModelScope.launch {
            val log = HabitLog(
                id = UUID.randomUUID().toString(),
                habitId = habit.id,
                date = LocalDate.now(),
                value = if (isCompleted) habit.targetValue else 0.0,
                isCompleted = isCompleted,
                notes = null,
                loggedAt = Instant.now()
            )
            repository.saveHabitLog(log)
        }
    }
}
