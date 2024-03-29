package com.github.se.studybuddies.ui.settings

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter

@Composable
fun AccountFields( usernameState: MutableState<String>) {
    Text("This is the username that other users will see.")
    Spacer(Modifier.height(20.dp))
    OutlinedTextField(
        value = usernameState.value,
        onValueChange = { usernameState.value = it },
        label = { Text("Username") },
        placeholder = { Text("Choose a username") },
        singleLine = true,
        modifier = Modifier
            .padding(0.dp)
            .width(300.dp)
            .height(65.dp))
}
@Composable
fun SetProfilePicture(photoState: MutableState<Uri>, onClick: () -> Unit) {
    Image(
        painter = rememberImagePainter(photoState.value),
        contentDescription = "Profile Picture",
        modifier = Modifier.size(100.dp),
        contentScale = ContentScale.Crop
    )
    Spacer(Modifier.height(20.dp))
    Text(
        text = "Select a profile picture",
        modifier = Modifier.clickable { onClick() }
    )
}
@Composable
fun SaveButton(usernameState: MutableState<String>, save: () -> Unit) {
    val enabled = usernameState.value.isNotEmpty()
    Button(
        onClick = save,
        enabled = enabled,
        modifier =
        Modifier.padding(0.dp)
            .width(300.dp)
            .height(50.dp)
            .background(color = Color.Transparent, shape = RoundedCornerShape(size = 10.dp))
            .testTag("todoSave"),
        colors =
        ButtonDefaults.buttonColors(
            containerColor = Color(0xFF8A8AF0),
        )) {
        Text("Save")
    }
}