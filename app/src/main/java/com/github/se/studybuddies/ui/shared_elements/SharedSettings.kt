package com.github.se.studybuddies.ui.shared_elements

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.github.se.studybuddies.R
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.ui.theme.White

/** Shared name setting element. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountFields(usernameState: MutableState<String>) {
  OutlinedTextField(
      value = usernameState.value,
      onValueChange = { usernameState.value = it },
      label = { Text(stringResource(R.string.username)) },
      placeholder = { Text(stringResource(R.string.enter_a_username)) },
      singleLine = true,
      modifier = Modifier.padding(0.dp).width(300.dp).height(65.dp).testTag("username_field"),
      colors =
          TextFieldDefaults.outlinedTextFieldColors(
              focusedBorderColor = Blue, unfocusedBorderColor = Blue, cursorColor = Blue))
  Spacer(Modifier.height(10.dp))
  Text(stringResource(R.string.msg_usename_user_will_see), modifier = Modifier.width(300.dp))
}

/** Shared profile picture setting element. */
@Composable
fun SetProfilePicture(photoState: MutableState<Uri>, onClick: () -> Unit) {
  Image(
      painter = rememberAsyncImagePainter(photoState.value),
      contentDescription = stringResource(R.string.profile_picture),
      modifier = Modifier.size(200.dp),
      contentScale = ContentScale.Crop)
  Spacer(Modifier.height(10.dp))
  Text(
      text = stringResource(R.string.select_a_profile_picture),
      modifier = Modifier.clickable { onClick() }.testTag("set_picture_button"))
}

/** Shared save button element. */
@Composable
fun SaveButton(
    usernameState: MutableState<String>,
    testTag: String = stringResource(R.string.save_button),
    save: () -> Unit
) {
  val enabled = usernameState.value.isNotEmpty()
  Button(
      onClick = save,
      enabled = enabled,
      modifier =
          Modifier.padding(10.dp)
              .width(300.dp)
              .height(50.dp)
              .background(color = Color.Transparent, shape = RoundedCornerShape(size = 10.dp))
              .testTag(testTag),
      colors =
          ButtonDefaults.buttonColors(
              containerColor = Blue,
          )) {
        Text(
            stringResource(R.string.save),
            color = White,
            modifier = Modifier.testTag("save_button_text"))
      }
}
