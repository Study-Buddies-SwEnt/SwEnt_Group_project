package com.github.se.studybuddies.ui.timer
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class TimerInfo(
    var duration: Long = 0L,  // Total duration in milliseconds
    var lastStartTime: Long = 0L,  // Last start time of the timer
    var isActive: Boolean = false  // Is the timer currently active?
) {
    // No-argument constructor for Firebase
    constructor() : this(0L, 0L, false)
}