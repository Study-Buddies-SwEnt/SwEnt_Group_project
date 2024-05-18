package com.github.se.studybuddies.data

import java.time.LocalDateTime

data class Event(
    val id: String,
    val name: String,
    val description: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val location: String?,
)
