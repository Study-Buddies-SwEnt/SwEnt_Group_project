package com.github.se.studybuddies.ui.video_call

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.github.se.studybuddies.database.ServiceLocator
import com.google.firebase.auth.FirebaseAuth
import io.getstream.log.Priority
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.core.StreamVideoBuilder
import io.getstream.video.android.core.logging.LoggingLevel
import io.getstream.video.android.model.User
import kotlinx.coroutines.flow.MutableStateFlow

enum class InitializedState {
  NOT_STARTED,
  RUNNING,
  FINISHED,
  FAILED
}

@SuppressLint("StaticFieldLeak")
object StreamVideoInitHelper {
  private lateinit var auth: FirebaseAuth

  private var isInitialising = false
  private lateinit var context: Context
  private val _initState = MutableStateFlow(InitializedState.NOT_STARTED)
  private val TAG: String = "StreamVideoInitHelper"

  fun init(appContext: Context) {
    context = appContext.applicationContext
  }

  suspend fun reloadSdk() {
    StreamVideo.removeClient()
    loadSdk()
  }

  /** A helper function that will initialise the [StreamVideo] SDK. */
  suspend fun loadSdk() {
    if (StreamVideo.isInstalled) {
      _initState.value = InitializedState.FINISHED
      Log.w(TAG, "[initStreamVideo] StreamVideo is already initialised.")
      return
    }

    if (isInitialising) {
      _initState.value = InitializedState.RUNNING
      Log.d(TAG, "[initStreamVideo] StreamVideo is already initialising")
      return
    }

    isInitialising = true
    _initState.value = InitializedState.RUNNING

    auth = FirebaseAuth.getInstance()
    val loggedInUser = auth.currentUser
    val username = ServiceLocator.provideDatabase().getCurrentUser().username

    try {
      // If we have a logged in user then we can initialise the SDK
      if (loggedInUser != null) {
        StreamVideoBuilder(
                context = context,
                apiKey = "x52wgjq8qyfc",
                user =
                    User(
                        id = loggedInUser.uid,
                        name = username,
                    ),
                token = StreamVideo.devToken(loggedInUser.uid),
                loggingLevel = LoggingLevel(priority = Priority.VERBOSE),
            )
            .build()
      }
      Log.i(TAG, "Init successful.")
      _initState.value = InitializedState.FINISHED
    } catch (e: Exception) {
      _initState.value = InitializedState.FAILED
      Log.e(TAG, "Init failed.", e)
    }

    isInitialising = false
  }
}
