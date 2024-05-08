package com.github.se.studybuddies.ui.settings

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.github.se.studybuddies.R

@Composable
fun AccountFields(usernameState: MutableState<String>) {
  Text(stringResource(R.string.msg_usename_user_will_see))
  Spacer(Modifier.height(20.dp))
  OutlinedTextField(
      value = usernameState.value,
      onValueChange = { usernameState.value = it },
      label = { Text(stringResource(R.string.username)) },
      placeholder = { Text(stringResource(R.string.enter_a_username)) },
      singleLine = true,
      modifier = Modifier.padding(0.dp).width(300.dp).height(65.dp).testTag("username_field"))
}

@Composable
fun SetProfilePicture(photoState: MutableState<Uri>, onClick: () -> Unit) {
  Image(
      painter = rememberImagePainter(photoState.value),
      contentDescription = stringResource(R.string.profile_picture),
      modifier = Modifier.size(200.dp),
      contentScale = ContentScale.Crop)
  Spacer(Modifier.height(20.dp))
  Text(
      text = stringResource(R.string.select_a_profile_picture),
      modifier = Modifier.clickable { onClick() }.testTag("set_picture_button"))
}
