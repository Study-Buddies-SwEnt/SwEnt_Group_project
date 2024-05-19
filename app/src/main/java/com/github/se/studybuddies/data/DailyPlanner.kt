package com.github.se.studybuddies.data

data class DailyPlanner(
    val date: String,
    val goals: List<String> = emptyList(),
    val appointments: Map<String, String> = emptyMap(),
    val notes: List<String> = emptyList()
)
