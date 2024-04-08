package com.github.se.studybuddies.ui.groups

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.studybuddies.ui.theme.Red

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupFields(nameState: MutableState<String>) {
  Spacer(Modifier.height(20.dp))
  OutlinedTextField(
      value = nameState.value,
      onValueChange = { nameState.value = it },
      label = { Text("Group Name",color = Red) },
      placeholder = { Text("Enter a group name", color = Red) },
      singleLine = true,
      modifier = Modifier
          .padding(0.dp)
          .width(300.dp)
          .height(65.dp)
          .clip(MaterialTheme.shapes.small),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Red,
            unfocusedBorderColor = Red,
            cursorColor = Red
        )
  )

}

@Composable
fun SaveButton(nameState: MutableState<String>, save: () -> Unit) {
  Button(
      onClick = save,
      enabled = nameState.value.isNotBlank(),
      modifier =
      Modifier
          .padding(0.dp)
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

@Composable
fun GroupTitle(title: String) {
  Text(
      title,
      modifier = Modifier
          .padding(0.dp)
          .testTag("todoTitle"),
      color = Red)
}
