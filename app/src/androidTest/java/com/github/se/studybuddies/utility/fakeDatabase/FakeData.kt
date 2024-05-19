package com.github.se.studybuddies.utility.fakeDatabase

import android.net.Uri
import com.github.se.studybuddies.data.Group
import com.github.se.studybuddies.data.TimerState
import com.github.se.studybuddies.data.User

var fakeUser1 =
    User(
        uid = "userTest1",
        email = "test@gmail.com",
        username = "testUser1",
        photoUrl = Uri.parse("https://images.pexels.com/photos/6031345/pexels-photo-6031345.jpeg"),
        location = "offline")
var fakeUser2 =
    User(
        uid = "userTest2",
        email = "test@gmail.com",
        username = "testUser2",
        photoUrl = Uri.parse("https://images.pexels.com/photos/6031345/pexels-photo-6031345.jpeg"),
        location = "47.5955829,7.7689383")

val fakeGroup1 =
    Group(
        uid = "groupTest1",
        name = "TestGroup1",
        picture = Uri.parse("https://images.pexels.com/photos/6031345/pexels-photo-6031345.jpeg"),
        members = mutableListOf(fakeUser1.uid),
        topics = mutableListOf("TestTopic"),
        timerState = TimerState(0L, false),
    )

val fakeUserDataCollection =
    mutableMapOf<String, User>().apply {
      put(fakeUser1.uid, fakeUser1)
      put(fakeUser2.uid, fakeUser2)
    }

val fakeUserMembershipsCollection =
    mutableMapOf<String, MutableList<String>>().apply {
      put(fakeUser1.uid, mutableListOf(fakeGroup1.uid))
    }

val fakeGroupDataCollection =
    mutableMapOf<String, Group>().apply { put(fakeGroup1.uid, fakeGroup1) }
