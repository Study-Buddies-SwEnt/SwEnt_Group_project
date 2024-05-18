package com.github.se.studybuddies.utility.fakeDatabase

import android.net.Uri
import com.github.se.studybuddies.data.Group
import com.github.se.studybuddies.data.TimerState
import com.github.se.studybuddies.data.User
import java.util.concurrent.ConcurrentHashMap

class FakeData {
}

var fakeUser1 =
    User(
        uid = "userTestE2E",
        email = "test@gmail.com",
        username = "testUser",
        photoUrl =
        Uri.parse("https://images.pexels.com/photos/6031345/pexels-photo-6031345.jpeg"),
        location = "offline")


val fakeGroup1 = Group(
    uid = "groupTestE2E",
    name = "TestGroup",
    picture = Uri.parse("https://images.pexels.com/photos/6031345/pexels-photo-6031345.jpeg"),
    members = mutableListOf(fakeUser1.uid),
    topics = mutableListOf("TestTopic"),
    timerState = TimerState(0L, false),
)


val fakeUserDataCollection = ConcurrentHashMap<String, User>().apply {
    put(fakeUser1.uid, fakeUser1)
}

val fakeUserMembershipsCollection = ConcurrentHashMap<String, MutableList<String>>().apply {
    put(fakeUser1.uid, mutableListOf(fakeGroup1.uid))
}



