package com.github.se.studybuddies.ui.todo

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//????
import com.github.se.studybuddies.R

import com.github.se.studybuddies.data.Location
import com.github.se.studybuddies.data.todo.ToDoStatus
import com.github.se.studybuddies.navigation.NavigationActions
import java.net.URLEncoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray

@Composable
fun TodoTopBar(navigationActions: NavigationActions, title: String) {
    val tag = if (title == "Create a new task") "createTodoTitle" else "editTodoTitle"
    TopAppBar(
        modifier = Modifier.width(412.dp).height(90.dp).padding(bottom = 2.dp),
        contentColor = Color.Transparent,
        backgroundColor = Color.Transparent,
        elevation = 0.dp) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.Top),
            horizontalAlignment = Alignment.Start,
        ) {
            IconButton(
                onClick = { navigationActions.goBack() },
                modifier = Modifier.testTag("goBackButton")) {
                Icon(
                    painterResource(R.drawable.arrow_back),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp))
            }
            Text(
                modifier = Modifier.width(360.dp).height(32.dp).padding(start = 16.dp).testTag(tag),
                text = title,
                style =
                TextStyle(
                    fontSize = 24.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight(400),
                ))
        }
    }
}

@Composable
fun TodoFields(
    titleState: MutableState<String>,
    descriptionState: MutableState<String>,
    assigneeState: MutableState<String>,
    onLocationChanged: (Location) -> Unit,
    locationQuery: MutableState<String>,
    selectedDate: MutableState<LocalDate>,
    isOpen: MutableState<Boolean>
) {
    OutlinedTextField(
        value = titleState.value,
        onValueChange = { titleState.value = it },
        label = { Text("Title") },
        placeholder = { Text("Name the task") },
        singleLine = true,
        modifier = Modifier.padding(0.dp).width(300.dp).height(65.dp).testTag("inputTodoTitle"))
    OutlinedTextField(
        value = descriptionState.value,
        onValueChange = { descriptionState.value = it },
        label = { Text("Description") },
        placeholder = { Text("Describe the task") },
        modifier =
        Modifier.padding(0.dp).width(300.dp).height(150.dp).testTag("inputTodoDescription"))
    OutlinedTextField(
        value = assigneeState.value,
        onValueChange = { assigneeState.value = it },
        label = { Text("Assignee") },
        placeholder = { Text("Assign a person") },
        singleLine = true,
        modifier = Modifier.padding(0.dp).width(300.dp).height(65.dp).testTag("inputTodoAssignee"))
    LocationTextField(onLocationChanged, locationQuery)
    /*
    OutlinedTextField(
        value = locationState.value,
        onValueChange = { locationState.value = it },
        label = { Text("Location") },
        placeholder = { Text("Enter an address") },
        singleLine = true,
        modifier = Modifier
            .padding(0.dp)
            .width(300.dp)
            .height(65.dp)
            .testTag("inputTodoLocation"))

       */
    Box() {
        OutlinedTextField(
            readOnly = true,
            value = selectedDate.value.format(DateTimeFormatter.ISO_DATE),
            label = { Text("Due date") },
            onValueChange = {},
            modifier = Modifier.width(300.dp).height(65.dp).testTag("inputTodoDate"),
            leadingIcon = {
                Icon(
                    painterResource(R.drawable.calendar),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp))
            })
        Box(modifier = Modifier.matchParentSize().alpha(0f).clickable { isOpen.value = true })
    }
}

@Composable
fun TodoSaveButton(titleState: MutableState<String>, save: () -> Unit) {
    val enabled = titleState.value.isNotEmpty()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(onAccept: (Long?) -> Unit, onCancel: () -> Unit) {
    val state = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = {},
        confirmButton = {
            Button(onClick = { onAccept(state.selectedDateMillis) }) { Text("Confirm") }
        },
        dismissButton = { Button(onClick = onCancel) { Text("Cancel") } }) {
        DatePicker(state = state)
    }
}

@Composable
fun LocationTextField(onLocationChanged: (Location) -> Unit, locationQuery: MutableState<String>) {
    val locationSuggestions = remember { mutableStateOf<List<String>>(emptyList()) }
    val expanded = remember { mutableStateOf(false) }

    Box(modifier = Modifier.testTag("locationDropDownMenuBox")) {
        OutlinedTextField(
            value = locationQuery.value,
            onValueChange = {
                locationQuery.value = it
                fetchLocationSuggestions(locationQuery.value) { suggestions ->
                    locationSuggestions.value = suggestions.take(4)
                    expanded.value = true
                }
            },
            label = { Text("Location") },
            placeholder = { Text("Enter an address") },
            singleLine = true,
            modifier =
            Modifier.padding(0.dp)
                .width(300.dp)
                .height(65.dp)
                .onFocusChanged {
                    if (!it.isFocused) {
                        expanded.value = false
                    }
                }
                .testTag("inputLocation"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            /*keyboardActions =
               KeyboardActions(
                   onDone = {
                     fetchLocationSuggestions(locationQuery.value) { suggestions ->
                       locationSuggestions.value = suggestions.take(4)
                       expanded.value = true
                     }
                   })
            */
        )
        DropdownMenu(
            expanded = expanded.value && locationSuggestions.value.isNotEmpty(),
            onDismissRequest = { expanded.value = false },
            modifier = Modifier.width(300.dp).testTag("locationDropDownMenu")) {
            locationSuggestions.value.forEach { suggestion ->
                DropdownMenuItem(
                    modifier = Modifier.testTag("inputLocationProposal"),
                    onClick = {
                        geocodeLocation(suggestion) { location ->
                            locationQuery.value = location.locationName
                            onLocationChanged(location)
                        }
                        expanded.value = false
                    }) {
                    Text(suggestion)
                }
                Divider(color = Color.Gray, thickness = 1.dp)
            }
        }
    }
}

@Composable
fun statusColor(status: ToDoStatus): Color {
    return when (status) {
        ToDoStatus.CREATED -> Color(0xFF9BC5C5)
        ToDoStatus.STARTED -> Color(0xFFFB9905)
        ToDoStatus.ENDED -> Color(0xFF1FC959)
        ToDoStatus.ARCHIVED -> Color(0xFF808080)
    }
}

private fun fetchLocationSuggestions(query: String, onComplete: (List<String>) -> Unit) {
    val client = OkHttpClient()
    val encodedQuery = URLEncoder.encode(query, "UTF-8")
    val request =
        Request.Builder()
            .url("https://nominatim.openstreetmap.org/search?format=json&q=$encodedQuery")
            .build()

    CoroutineScope(Dispatchers.IO).launch {
        val response = client.newCall(request).execute()
        val body = response.body()?.string()
        val suggestions = parseLocationSuggestions(body)
        withContext(Dispatchers.Main) { onComplete(suggestions) }
    }
}

private fun parseLocationSuggestions(response: String?): List<String> {
    val suggestions = mutableListOf<String>()
    response?.let {
        val jsonArray = JSONArray(it)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val displayName = jsonObject.getString("display_name")
            suggestions.add(displayName)
        }
    }
    return suggestions
}

private fun geocodeLocation(query: String, onComplete: (Location) -> Unit) {
    val client = OkHttpClient()
    val encodedQuery = URLEncoder.encode(query, "UTF-8")
    val request =
        Request.Builder()
            .url("https://nominatim.openstreetmap.org/search?format=json&q=$encodedQuery")
            .build()

    client
        .newCall(request)
        .enqueue(
            object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                    Log.e("MyPrint", "Failed to fetch location: $e")
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    val body = response.body()?.string()
                    if (body != null) {
                        val jsonArray = JSONArray(body)
                        if (jsonArray.length() > 0) {
                            val jsonObject = jsonArray.getJSONObject(0)
                            val latitude = jsonObject.getDouble("lat")
                            val longitude = jsonObject.getDouble("lon")
                            val name = jsonObject.getString("display_name")
                            val location = Location(latitude, longitude, name)
                            Log.d("MyPrint", "Location: $location")
                            onComplete(location)
                        } else {
                            Log.d("MyPrint", "No location found")
                        }
                    } else {
                        Log.d("MyPrint", "No response body")
                    }
                }
            })
}