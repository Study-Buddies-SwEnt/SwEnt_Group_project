package com.github.se.studybuddies.testUtilities

import android.os.Bundle
import com.github.se.studybuddies.MainActivity
import com.github.se.studybuddies.database.MockDatabase

class MockMainActivity : MainActivity() {
  private val fakeUid: String = "E2EUserTest"

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    db = MockDatabase()
    startApp(fakeUid, db)
  }

  override fun onStop() {
    super.onStop()
    db = MockDatabase()
    offlineLocation(fakeUid, db)
  }
}
