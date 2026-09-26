package com.habitpulse.feature.analytics.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun HeatmapCalendar(
    heatmapData: Map<LocalDate, Float>,
    modifier: Modifier = Modifier,
    startDate: LocalDate = LocalDate.now().minusDays(364)
) {
    Canvas(modifier = modifier.fillMaxWidth().height(150.dp)) {
        val daysInYear = 365
        val cols = 52
        val rows = 7

        val cellWidth = size.width / cols
        val cellHeight = size.height / rows
        val cellSize = minOf(cellWidth, cellHeight) * 0.8f
        val paddingX = (cellWidth - cellSize) / 2
        val paddingY = (cellHeight - cellSize) / 2

        var currentDate = startDate

        for (col in 0 until cols) {
            for (row in 0 until rows) {
                if (currentDate.isAfter(LocalDate.now())) break

                val completionRatio = heatmapData[currentDate] ?: 0f
                val color = when {
                    completionRatio >= 1.0f -> Color(0xFF228B22) // ForestGreen
                    completionRatio > 0.0f -> Color(0xFF90EE90) // LightGreen
                    else -> Color(0xFFE0E0E0) // Gray
                }

                val x = col * cellWidth + paddingX
                val y = row * cellHeight + paddingY

                drawRoundRect(
                    color = color,
                    topLeft = Offset(x, y),
                    size = Size(cellSize, cellSize),
                    cornerRadius = CornerRadius(4f, 4f)
                )

                currentDate = currentDate.plusDays(1)
            }
        }
    }
}
