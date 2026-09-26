package com.habitpulse.feature.dashboard

import com.habitpulse.core.domain.model.Habit

sealed interface DashboardUiState {
    object Loading : DashboardUiState
    data class Success(val habits: List<Habit>) : DashboardUiState
    object Empty : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}
