package com.github.se.studybuddies.tests

// @RunWith(AndroidJUnit4::class)
class CallLobbyTest {
  /*: TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

    @get:Rule val composeTestRule = createComposeRule()

    @get:Rule
    val permissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO,
        )

    @get:Rule val mockkRule = MockKRule(this)
    @RelaxedMockK lateinit var mockNavActions: NavigationActions

    private val uid = "111test"
    private val userID = "testUser"

    @Before
    fun setup() {
      val context: Context = ApplicationProvider.getApplicationContext()
      getInstrumentation()
          .uiAutomation
          .grantRuntimePermission("com.github.se.studybuddies.tests", "android.permission.CAMERA")
      getInstrumentation()
          .uiAutomation
          .grantRuntimePermission(
              "com.github.se.studybuddies.tests", "android.permission.RECORD_AUDIO")
      if (StreamVideo.isInstalled) {
        StreamVideo.removeClient()
      }
      StreamVideoBuilder(
              context = context,
              apiKey = "x52wgjq8qyfc",
              user =
                  User(
                      id = userID,
                      name = "test",
                  ),
              token =
                  "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiSm9ydXVzX0NfQmFvdGgiLCJpc3MiOiJodHRwczovL3Byb250by5nZXRzdHJlYW0uaW8iLCJzdWIiOiJ1c2VyL0pvcnV1c19DX0Jhb3RoIiwiaWF0IjoxNzE0NjUzOTg0LCJleHAiOjE3MTUyNTg3ODl9.WkUHrFvbIdfjqKIcxi4FQB6GmQB1q0uyQEAfJ61P_g0")
          .build()
      val callVM = CallLobbyViewModel(uid, "default")

      composeTestRule.setContent { CallLobbyScreen(uid, callVM, mockNavActions) }
    }

    @Test
    fun elementsAreDisplayed() {
      ComposeScreen.onComposeScreen<CallLobbyScreen>(composeTestRule) {
        runBlocking {
          delay(10000) // Adjust the delay time as needed
        }
        content { assertIsDisplayed() }
        topAppBar { assertIsDisplayed() }
        callIcon { assertIsDisplayed() }
        previewText { assertIsDisplayed() }
        callLobby { assertIsDisplayed() }
        joinCallButton {
          assertIsDisplayed()
          assertHasClickAction()
        }
      }
    }

    @After
    fun afterTest() {
      StreamVideo.removeClient()
    }
  */
}
