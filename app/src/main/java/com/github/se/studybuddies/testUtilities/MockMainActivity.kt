package com.github.se.studybuddies.testUtilities

import android.os.Bundle
import com.github.se.studybuddies.MainActivity
import com.github.se.studybuddies.database.MockDatabase

class MockMainActivity : MainActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val fakeUID = "E2EUserTest"
    db = MockDatabase()
    startApp(fakeUID, db)
  }

  override fun onStop() {
    super.onStop()
    val fakeUID = "E2EUserTest"
    db = MockDatabase()
    offlineLocation(fakeUID, db)
  }
}
