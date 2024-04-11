package com.github.se.studybuddies.ui

import android.util.Log
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.github.se.studybuddies.data.Message
import com.github.se.studybuddies.data.User
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.viewModels.DatabaseConnection
import com.github.se.studybuddies.viewModels.MessageViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@Composable
fun ChatScreen(viewModel: MessageViewModel, navigationActions: NavigationActions) {
  val messages = viewModel.messages.collectAsState().value
  var text by remember { mutableStateOf("") }

  // TODO issue when open keyboard, the list of messages goes up

  Scaffold(
      bottomBar = {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.padding(8.dp).fillMaxWidth().navigationBarsPadding(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
            keyboardActions =
                KeyboardActions(
                    onSend = {
                      if (text.isNotBlank()) {
                        viewModel.sendMessage(text)
                        text = ""
                      }
                    }))
      }) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize()) {
          items(messages) { message ->
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment =
                    if (viewModel.isUserMessageSender(message)) {
                      Alignment.CenterEnd
                    } else {
                      Alignment.CenterStart
                    }) {
                  Text(text = message.text, modifier = Modifier.padding(8.dp))
                }
          }
        }
      }
}

@Preview
@Composable
fun ChatScreenPreview() {
  val groupUID = "groupUID_test_1"
  ChatScreen(MessageViewModel(groupUID), NavigationActions(rememberNavController()))
}

// TODO - remove it when totaly working
// Test functions for help to program the chat
@Preview
@Composable
fun PreviewReceiveMessage() {
  //    myRef.get().addOnSuccessListener {
  //        val value = it.getValue(String::class.java)
  //        Log.d("TAG - chat", "Value is: $value")
  //    }.addOnFailureListener {
  //        Log.w("TAG - chat", "Failed to read value.", it)
  //    }
  val db = DatabaseConnection()

  val myRef =
      Firebase.database(
              "https://study-buddies-e655a-default-rtdb.europe-west1.firebasedatabase.app/")
          .getReference("message")

  myRef.addValueEventListener(
      object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
          //            val value = dataSnapshot.getValue(String::class.java)
          //            Log.d("TAG - chat", "Value is: $value")

          for (postSnapshot in dataSnapshot.children) {
            val message =
                Message(
                    postSnapshot.key.toString(),
                    postSnapshot.child("text").value.toString(),
                    db.getUser(postSnapshot.child("senderId").value.toString()),
                    postSnapshot.child("timestamp").value.toString().toLong())
            Log.d(
                "TAG - chat",
                "ID : ${message.uid}, Message: ${message.text}, Sender: ${message.sender.username}, Timestamp: ${message.timestamp} ")
          }
        }

        override fun onCancelled(error: DatabaseError) {
          // Failed to read
          Log.w("TAG - chat", "Failed to read value.", error.toException())
        }
      })
}

@Preview
@Composable
fun PreviewSendMessage() {
  /*//myRef.setValue("Hello, World!")

      // send data to realtime database in as messageID, and assign to it the text, senderId and timestamp
      //myRef.child("messageID").setValue("msgID")
      val x = "messageID_2"
      myRef.child(x).child("text").setValue("Hello, World!")
      myRef.child(x).child("senderId").setValue("LK1DcILy5gbYAMDi2ymTZZPnX6l1")
  //    myRef.child(x).child("senderId").setValue("J8TU0lj0EhVBwMMBb21KKVlrC9G2")
      myRef.child(x).child("timestamp").setValue("11.04.2024")
      Log.d("TAG - chat", "Data sent to realtime database")*/

  val db = DatabaseConnection()
  db.sendGroupMessage(
      "groupID_2",
      Message(
          text = "Hi there new ", sender = User.empty(), timestamp = System.currentTimeMillis()))
  Log.d("TAG - chat", "Data sent to realtime database")
}
