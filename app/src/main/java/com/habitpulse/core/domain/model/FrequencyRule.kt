package com.habitpulse.core.domain.model

sealed class FrequencyRule {
    object Daily : FrequencyRule()
    data class Weekly(val daysPerWeek: Int) : FrequencyRule()
    data class SpecificDays(val daysOfWeek: Set<Int>) : FrequencyRule() // 1 = Monday, 7 = Sunday
}
