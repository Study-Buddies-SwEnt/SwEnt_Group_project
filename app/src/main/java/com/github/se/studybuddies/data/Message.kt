package com.github.se.studybuddies.data

import android.annotation.SuppressLint
import android.net.Uri
import java.util.UUID

sealed class Message {
  abstract val uid: String
  abstract val sender: User
  abstract val timestamp: Long

  @SuppressLint("SimpleDateFormat")
  fun getTime(): String {
    val date = java.util.Date(timestamp)
    val time = java.text.SimpleDateFormat("HH:mm").format(date)
    return time
  }

  @SuppressLint("SimpleDateFormat")
  fun getDate(): String {
    val date = java.util.Date(timestamp)
    val time = java.text.SimpleDateFormat("dd MMMM").format(date)
    return time
  }

  companion object {
    fun empty(): TextMessage = TextMessage(text = "", sender = User.empty(), timestamp = 0)
  }

  data class TextMessage(
      override val uid: String = UUID.randomUUID().toString(),
      val text: String,
      override val sender: User,
      override val timestamp: Long,
  ) : Message()

  data class PhotoMessage(
      override val uid: String = UUID.randomUUID().toString(),
      val photoUri: Uri,
      override val sender: User,
      override val timestamp: Long,
  ) : Message()

  data class LinkMessage(
      override val uid: String = UUID.randomUUID().toString(),
      val linkUri: Uri,
      override val sender: User,
      override val timestamp: Long,
  ) : Message()

  data class FileMessage(
      override val uid: String = UUID.randomUUID().toString(),
      val fileUri: Uri,
      override val sender: User,
      override val timestamp: Long,
  ) : Message()

  /*   data class AudioMessage(
       override val uid: String = UUID.randomUUID().toString(),
       val audioUri: Uri,
       override val sender: User,
       override val timestamp: Long,
   ) : Message()

   data class VideoMessage(
       override val uid: String = UUID.randomUUID().toString(),
       val videoUri: Uri,
       override val sender: User,
       override val timestamp: Long,
   ) : Message()

   data class LocationMessage(
       override val uid: String = UUID.randomUUID().toString(),
       val latitude: Double,
       val longitude: Double,
       override val sender: User,
       override val timestamp: Long,
   ) : Message()

  data class ContactMessage(
       override val uid: String = UUID.randomUUID().toString(),
       val contact: User,
       override val sender: User,
       override val timestamp: Long,
   ) : Message()

   data class PollMessage(
       override val uid: String = UUID.randomUUID().toString(),
       val question: String,
       val options: List<String>,
       override val sender: User,
       override val timestamp: Long,
   ) : Message()

   data class StickerMessage(
       override val uid: String = UUID.randomUUID().toString(),
       val stickerUri: Uri,
       override val sender: User,
       override val timestamp: Long,
   ) : Message()

   data class GifMessage(
       override val uid: String = UUID.randomUUID().toString(),
       val gifUri: Uri,
       override val sender: User,
       override val timestamp: Long,
   ) : Message()*/
}

object MessageVal {
  // name of the fields in the database
  const val SENDER_UID = "senderId"
  const val TIMESTAMP = "timestamp"
  const val TYPE = "type"
  const val TEXT = "text"
  const val PHOTO = "photoUri"
  const val LINK = "linkUri"
  const val FILE = "fileUri"
}
