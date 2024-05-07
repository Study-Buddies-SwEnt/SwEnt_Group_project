package com.github.se.studybuddies.viewModels

import androidx.lifecycle.ViewModel
import io.getstream.video.android.core.Call

class CallLobbyViewModel (val call: Call, val uid: String) : ViewModel() {

    fun acceptCall() {
    }
}
