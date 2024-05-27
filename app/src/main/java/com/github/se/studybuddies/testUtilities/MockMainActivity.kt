package com.github.se.studybuddies.testUtilities

import android.os.Bundle
import com.github.se.studybuddies.MainActivity
import com.github.se.studybuddies.testUtilities.fakeDatabase.MockDatabase

class MockMainActivity : MainActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    var fakeUID = "E2EUserTest"
    db = MockDatabase()
    startApp(fakeUID, db)
  }
}
