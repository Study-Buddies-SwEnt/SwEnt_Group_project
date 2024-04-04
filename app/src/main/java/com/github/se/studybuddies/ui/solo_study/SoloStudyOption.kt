package com.github.se.studybuddies.ui.solo_study

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.studybuddies.data.Group
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.theme.Red
import com.github.se.studybuddies.ui.theme.White

data class SoloStudyOption(val name: String, val icon : ImageVector, val route : String) {}

class SoloStudyOptionList(private val SoloStudyOptions: List<SoloStudyOption>) {
    fun getAllOptions(): List<SoloStudyOption> {
        return SoloStudyOptions
    }
}

val FLASH_CARD = SoloStudyOption("Flash Card", Icons.Filled.AccountBox, Route.FLASHCARD)
val TODO_LIST = SoloStudyOption("Todo List", Icons.Filled.Check, Route.TODOLIST)
val FOCUS_MODE = SoloStudyOption("Focus Mode", Icons.Filled.Lock, Route.FOCUSMODE)
val TIMER = SoloStudyOption("Timer", Icons.Filled.Refresh, Route.TIMER)
val CALENDAR = SoloStudyOption("Calendar", Icons.Filled.DateRange, Route.CALENDAR)

@Composable
fun solo_study_buttons(option: SoloStudyOption) {
    Button(onClick = { /*TODO*/ },
        modifier = Modifier
            .height(240.dp)
            .width(280.dp),
        colors = ButtonDefaults.buttonColors(White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = option.icon,
                contentDescription = option.name,
                modifier = Modifier
                    .height(160.dp)
                    .width(160.dp)
            )
            Text("Flash card", color = Red, fontSize = 20.sp)
        }
    }
}