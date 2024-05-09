package com.github.se.studybuddies

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.github.se.studybuddies.ui.screens.VideoCallScreen
import io.getstream.result.Result
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.core.notifications.NotificationHandler
import io.getstream.video.android.model.StreamCallId
import io.getstream.video.android.model.streamCallId
import kotlinx.coroutines.launch

class CallActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

    // step 1 - get the StreamVideo instance and create a call
    val streamVideo = StreamVideo.instance()
    val cid =
        intent.streamCallId(EXTRA_CID)
            ?: throw IllegalArgumentException("call type and id is invalid!")

    // optional - check for already active call that can be utilized
    // This step is optional and can be skipped
    // val cal = streamVideo.call(type = cid.type, id = cid.id)
    val activeCall = streamVideo.state.activeCall.value
    val call =
        if (activeCall != null) {
          if (activeCall.id != cid.id) {
            Log.w("CallActivity", "A call with id: ${cid.cid} existed. Leaving.")
            // If the call id is different leave the previous call
            activeCall.leave()
            // Return a new call
            streamVideo.call(type = cid.type, id = cid.id)
          } else {
            // Call ID is the same, use the active call
            activeCall
          }
        } else {
          // There is no active call, create new call
          streamVideo.call(type = cid.type, id = cid.id)
        }

    // optional - call settings. We disable the mic if coming from QR code demo
    if (intent.getBooleanExtra(EXTRA_DISABLE_MIC_BOOLEAN, false)) {
      call.microphone.disable(true)
    }

    // step 2 - join a call
    lifecycleScope.launch {
      // If the call is new, join the call
      if (activeCall != call) {
        val result = call.join(create = true)

        // Unable to join. Device is offline or other usually connection issue.
        if ((result is Result.Failure)) {
          Log.e("CallActivity", "Call.join failed ${result.value}")
          Toast.makeText(
                  this@CallActivity,
                  "Failed to join call (${result.value.message})",
                  Toast.LENGTH_SHORT,
              )
              .show()
          finish()
        }
      }
    }

    // step 3 - build a call screen
    setContent {
      VideoCallScreen(
          call = call,
          onCallDisconnected = {
            // call state changed to disconnected - we can leave the screen
            goBackToMainScreen()
          },
          onUserLeaveCall = {
            call.leave()
            // we don't need to wait for the call state to change to disconnected, we can
            // leave immediately
            goBackToMainScreen()
          },
      )
    }
  }

  private fun goBackToMainScreen() {
    if (!isFinishing) {
      val intent =
          Intent(this@CallActivity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
          }
      startActivity(intent)
      finish()
    }
  }

  companion object {
    const val EXTRA_CID: String = NotificationHandler.INTENT_EXTRA_CALL_CID
    const val EXTRA_DISABLE_MIC_BOOLEAN: String = "EXTRA_DISABLE_MIC"

    /**
     * @param callId the Call ID you want to join
     * @param disableMicOverride optional parameter if you want to override the users setting and
     *   disable the microphone.
     */
    @JvmStatic
    fun createIntent(
        context: Context,
        callId: StreamCallId,
        disableMicOverride: Boolean = false,
    ): Intent {
      return Intent(context, CallActivity::class.java).apply {
        putExtra(EXTRA_CID, callId)
        putExtra(EXTRA_DISABLE_MIC_BOOLEAN, disableMicOverride)
      }
    }
  }
}
