package com.github.se.studybuddies.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.firebase.ui.auth.AuthUI
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.viewModels.UserViewModel


@Composable
fun AccountSettings(uid: String, userViewModel: UserViewModel, navigationActions: NavigationActions) {
    userViewModel.fetchUserData(uid)
    val userData by userViewModel.userData.observeAsState()

    val emailState = remember { mutableStateOf(userData?.email ?: "") }
    val usernameState = remember { mutableStateOf(userData?.username ?: "") }
    val photoState = remember { mutableStateOf(userData?.photoUrl ?: Uri.EMPTY) }

    userData?.let {
        emailState.value = it.email
        usernameState.value = it.username
        photoState.value = it.photoUrl
    }


    val getContent = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { profilePictureUri ->
            photoState.value = profilePictureUri
            userViewModel.updateUserData(
                userViewModel.getCurrentUserUID(),
                emailState.value,
                usernameState.value,
                photoState.value
            )
        }
    }

    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center)
    {
        SignOutButton(navigationActions)
        Spacer(Modifier.height(150.dp))
        SetProfilePicture(photoState) { getContent.launch("image/*") }
    }


}

@Composable
private fun SignOutButton(navigationActions: NavigationActions) {
    val context = LocalContext.current // Get the context here
    Button(
        onClick = {
            AuthUI.getInstance().signOut(context).addOnCompleteListener {
                if (it.isSuccessful) {
                    navigationActions.navigateTo(Route.LOGIN)
                }
            }

        },
        colors =
        ButtonDefaults.buttonColors(
            containerColor = Color.White,
        ),
        modifier =
        Modifier
            .border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(50))
            .background(color = Color.Transparent, shape = RoundedCornerShape(50))
            .width(250.dp)
            .height(50.dp)
            .testTag("LoginButton"),
        shape = RoundedCornerShape(50)
    ) {
        Text("Sign out", color = Color.Black)
    }
}