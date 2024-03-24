package com.github.se.studybuddies.viewModels

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.time.ZoneId
import java.util.Date
import kotlinx.coroutines.tasks.await

class FirebaseConnection {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val userData = db.collection("userData")

    fun getCurrentUserUID(): String {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        return if (uid != null) {
            uid
        } else {
            Log.d("ErrorPrint", "Failed to get current user UID")
            ""
        }
    }

    fun getUserData(uid: String): Task<DocumentSnapshot> {
        return userData.document(uid).get()
    }

    /*
    fun updateTodo(
        todoId: String,
        name: String,
        assigneeName: String,
        dueDate: Date,
        location: String,
        description: String,
        status: String
    ) {
        val task =
            hashMapOf(
                "title" to name,
                "assigneeName" to assigneeName,
                "dueDate" to dueDate,
                "location" to location,
                "description" to description,
                "status" to status)
        todoCollection
            .document(todoId)
            .update(task as Map<String, Any>)
            .addOnSuccessListener { Log.d("MyPrint", "Task $todoId succesfully updated") }
            .addOnFailureListener { Log.d("MyPrint", "Task $todoId failed to update") }
    }

    suspend fun getAllItems(): ToDoList {
        val querySnapshot = todoCollection.get().await()
        val items = mutableListOf<ToDo>()

        for (document in querySnapshot.documents) {
            val uid = document.id
            val name = document.getString("title") ?: ""
            val assigneeName = document.getString("assigneeName") ?: ""
            val dueDate = document.getDate("dueDate")
            val convertedDate = dueDate!!.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            val locationString = document.getString("location") ?: ""
            val location = Location.fromString(locationString)
            val description = document.getString("description") ?: ""
            val status = ToDoStatus.valueOf(document.getString("status") ?: "")

            val item = ToDo(uid, name, assigneeName, convertedDate, location, description, status)
            items.add(item)
        }

        return ToDoList(items)
    }

    fun addNewTodo(
        name: String,
        assigneeName: String,
        dueDate: Date,
        location: String,
        description: String,
        status: String
    ) {
        val task =
            hashMapOf(
                "title" to name,
                "assigneeName" to assigneeName,
                "dueDate" to dueDate,
                "location" to location,
                "description" to description,
                "status" to status)
        todoCollection
            .add(task)
            .addOnSuccessListener { Log.d("MyPrint", "Task succesfully added") }
            .addOnFailureListener { Log.d("MyPrint", "Failed to add task") }
    }

    fun fetchTaskByUID(uid: String): Task<DocumentSnapshot> {
        return todoCollection.document(uid).get()
    }

    fun deleteTodo(todoId: String) {
        todoCollection
            .document(todoId)
            .delete()
            .addOnSuccessListener { Log.d("MyPrint", "Successfully deleted task") }
            .addOnFailureListener { Log.d("MyPrint", "Failed to delete task") }
    }
     */
}
