package com.github.se.studybuddies.ui

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.github.se.studybuddies.R
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.theme.Red
import com.github.se.studybuddies.viewModels.DatabaseConnection
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(navigationActions: NavigationActions) {
  val signInLauncher =
      rememberLauncherForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
        onSignInResult(res, navigationActions)
      }
    val Playball = FontFamily(Font(R.font.playball_regular))

    Column(
      modifier = Modifier.fillMaxSize().testTag("LoginScreen"),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center) {
        Image(
            painter = painterResource(R.drawable.main_logo),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.width(189.dp).height(189.dp))
        Spacer(Modifier.height(67.dp))
        Text(
            text = "Study Buddies",
            style =
                TextStyle(
                    fontFamily = Playball,
                    fontSize = 52.sp,
                    fontWeight = FontWeight(700),
                    textAlign = TextAlign.Center,
                ),
            modifier = Modifier.width(360.dp).height(120.dp).testTag("LoginTitle"))
        Spacer(Modifier.height(150.dp))
        Button(
            onClick = {
              val signInIntent =
                  AuthUI.getInstance()
                      .createSignInIntentBuilder()
                      .setAvailableProviders(arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build()))
                      .setIsSmartLockEnabled(false)
                      .build()
              signInLauncher.launch(signInIntent)
            },
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                ),
            modifier =
                Modifier.border(width = 2.dp, color = Red, shape = RoundedCornerShape(50))
                    .background(color = Color.Transparent, shape = RoundedCornerShape(50))
                    .width(302.dp)
                    .height(76.dp)
                    .testTag("LoginButton"),
            shape = RoundedCornerShape(50)) {
              Image(
                  painter = painterResource(R.drawable.google),
                  contentDescription = null,
                  modifier = Modifier.size(40.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Sign in with Google", color = Red)
            }
      }
}

private fun onSignInResult(
    result: FirebaseAuthUIAuthenticationResult,
    navigationActions: NavigationActions
) {
  if (result.resultCode == Activity.RESULT_OK) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    if (userId != null) {
      val db = DatabaseConnection()
      db.userExists(
          userId,
          onSuccess = { userExists ->
            if (!userExists) {
              navigationActions.navigateTo(Route.CREATEACCOUNT)
            } else {
              navigationActions.navigateTo(Route.GROUPSHOME)
            }
          },
          onFailure = { e -> Log.d("MyPrint", "Failed to check user existence with error: $e") })
    } else {
      Log.e("MyPrint", "Failed to get user ID")
    }
    Log.d("MyPrint", "Sign in successful")
  } else {
    Log.d("MyPrint", "Sign in failed")
  }
}
