package com.github.se.studybuddies.ui.groups

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.se.studybuddies.R
import com.github.se.studybuddies.ui.theme.Blue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupFields(nameState: MutableState<String>) {
  Spacer(Modifier.height(20.dp))
  OutlinedTextField(
      value = nameState.value,
      onValueChange = { nameState.value = it },
      label = { Text(stringResource(R.string.group_name), color = Blue) },
      placeholder = { Text(stringResource(R.string.enter_a_group_name), color = Blue) },
      singleLine = true,
      modifier =
          Modifier.padding(0.dp)
              .width(300.dp)
              .height(65.dp)
              .clip(MaterialTheme.shapes.small)
              .testTag("group_name_field"),
      colors =
          TextFieldDefaults.outlinedTextFieldColors(
              focusedBorderColor = Blue, unfocusedBorderColor = Blue, cursorColor = Blue))
}
