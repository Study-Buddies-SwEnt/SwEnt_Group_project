package com.github.se.studybuddies.database

import android.net.Uri
import com.github.se.studybuddies.data.Contact
import com.github.se.studybuddies.data.Group
import com.github.se.studybuddies.data.TimerState
import com.github.se.studybuddies.data.Topic
import com.github.se.studybuddies.data.TopicFile
import com.github.se.studybuddies.data.TopicFolder
import com.github.se.studybuddies.data.TopicItem
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
        email = "test2@gmail.com",
        username = "testUser2",
        photoUrl = Uri.parse("https://images.pexels.com/photos/6031345/pexels-photo-6031345.jpeg"),
        location = "47.5955829,7.7689383")

var fakeUser3 =
    User(
        uid = "userTest3",
        email = "test3@gmail.com",
        username = "testUser3",
        photoUrl = Uri.parse("https://images.pexels.com/photos/6031345/pexels-photo-6031345.jpeg"),
        location = "47.5955829,7.7689383")

val fakExFile1 =
    TopicFile(
        uid = "exItemTest1",
        fileName = "exTestItem1",
        strongUsers = mutableListOf("userTest1"),
        parentUID = "exFolderTest1")

val fakeExTopicFolder =
    TopicFolder(
        uid = "exFolderTest1",
        folderName = "exTestFolder",
        items = mutableListOf(fakExFile1),
        parentUID = "topicTest1")

val fakeExFile2 =
    TopicFile(
        uid = "exItemTest2",
        fileName = "exTestItem2",
        strongUsers = mutableListOf("userTest1"),
        parentUID = "topicTest1")

val fakeTeFile1 =
    TopicFile(
        uid = "teItemTest1",
        fileName = "teTestItem",
        strongUsers = mutableListOf("userTest1"),
        parentUID = "teFolderTest1")

val fakeTeFolder =
    TopicFolder(
        uid = "teFolderTest1",
        folderName = "teTestFolder",
        items = mutableListOf(fakeTeFile1),
        parentUID = "topicTest1")

val fakeTopic1 =
    Topic(
        uid = "topicTest1",
        name = "TestTopic",
        exercises = mutableListOf(fakeExTopicFolder, fakeExFile2),
        theory = mutableListOf(fakeTeFolder))

val fakeGroup1 =
    Group(
        uid = "groupTest1",
        name = "TestGroup1",
        picture = Uri.parse("https://images.pexels.com/photos/6031345/pexels-photo-6031345.jpeg"),
        members = mutableListOf(fakeUser1.uid),
        topics = mutableListOf(fakeTopic1.uid),
        timerState = TimerState(0L, false),
    )

val fakeContact1 =
    Contact(
        id = "contactTest1",
        members = mutableListOf(fakeUser1.uid, fakeUser2.uid),
        showOnMap = true,
        hasStartedDM = false)
val fakeGroup2 =
    Group(
        uid = "groupTest2",
        name = "TestGroup2",
        picture = Uri.parse("https://images.pexels.com/photos/6031345/pexels-photo-6031345.jpeg"),
        members = mutableListOf(fakeUser3.uid),
        topics = mutableListOf(fakeTopic1.uid),
        timerState = TimerState(0L, false),
    )

val fakeUserDataCollection =
    mutableMapOf<String, User>().apply {
      put(fakeUser1.uid, fakeUser1)
      put(fakeUser2.uid, fakeUser2)
      put(fakeUser3.uid, fakeUser3)
    }

val fakeUserMembershipsCollection =
    mutableMapOf<String, MutableList<String>>().apply {
      put(fakeUser1.uid, mutableListOf(fakeGroup1.uid))
      put(fakeUser2.uid, mutableListOf<String>())
      put(fakeUser3.uid, mutableListOf(fakeGroup2.uid))
    }

val fakeContactDataCollection =
    mutableMapOf<String, Contact>().apply { put(fakeContact1.id, fakeContact1) }

val fakeUserContactCollection =
    mutableMapOf<String, MutableList<String>>().apply {
      put(fakeUser1.uid, mutableListOf(fakeContact1.id))
      put(fakeUser2.uid, mutableListOf(fakeContact1.id))
    }

val fakeGroupDataCollection =
    mutableMapOf<String, Group>().apply {
      put(fakeGroup1.uid, fakeGroup1)
      put(fakeGroup2.uid, fakeGroup2)
    }

val fakeTopicDataCollection =
    mutableMapOf<String, Topic>().apply { put(fakeTopic1.uid, fakeTopic1) }

val fakeTopicItemCollection =
    mutableMapOf<String, TopicItem>().apply {
      put(fakExFile1.uid, fakExFile1)
      put(fakeExFile2.uid, fakeExFile2)
      put(fakeTeFile1.uid, fakeTeFile1)
      put(fakeExTopicFolder.uid, fakeExTopicFolder)
      put(fakeTeFolder.uid, fakeTeFolder)
    }
