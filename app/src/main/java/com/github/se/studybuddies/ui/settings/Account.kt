package com.github.se.studybuddies.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.firebase.ui.auth.AuthUI
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.TOP_LEVEL_DESTINATIONS

@Composable
fun SignOutButton(navigationActions: NavigationActions) {
    val context = LocalContext.current // Get the context here
    Button(
        onClick = {
            AuthUI.getInstance().signOut(context).addOnCompleteListener {
                    if (it.isSuccessful) {
                        navigationActions.navigateTo(TOP_LEVEL_DESTINATIONS[0])
                    }
                }

        },
        colors =
        ButtonDefaults.buttonColors(
            containerColor = Color.White,
        ),
        modifier =
        Modifier.border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(50))
            .background(color = Color.Transparent, shape = RoundedCornerShape(50))
            .width(250.dp)
            .height(50.dp)
            .testTag("LoginButton"),
        shape = RoundedCornerShape(50)
    ) {
        Text("Sign out", color = Color.Black)
    }
}