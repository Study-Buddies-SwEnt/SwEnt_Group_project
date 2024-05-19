package com.github.se.studybuddies.tests

// @RunWith(AndroidJUnit4::class)
class VideoCallTest /*: TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

                    @get:Rule val composeTestRule = createComposeRule()

                    @get:Rule
                    val permissionRule: GrantPermissionRule =
                        GrantPermissionRule.grant(
                            android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO)

                    private val uid = "111test"
                    private val userID = "testUser"

                    @Before
                    fun testSetup() {
                      val context: Context = ApplicationProvider.getApplicationContext()
                      InstrumentationRegistry.getInstrumentation()
                          .uiAutomation
                          .grantRuntimePermission("com.github.se.studybuddies.tests", "android.permission.CAMERA")
                      InstrumentationRegistry.getInstrumentation()
                          .uiAutomation
                          .grantRuntimePermission(
                              "com.github.se.studybuddies.tests", "android.permission.RECORD_AUDIO")

                      if (StreamVideo.isInstalled) {
                        StreamVideo.removeClient()
                      }
                      runBlocking {
                        StreamVideoBuilder(
                                context = context,
                                apiKey = "x52wgjq8qyfc",
                                user =
                                    User(
                                        id = userID,
                                        name = "test",
                                    ),
                                token =
                                    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiSm9ydXVzX0NfQmFvdGgiLCJpc3MiOiJodHRwczovL3Byb250by5nZXRzdHJlYW0uaW8iLCJzdWIiOiJ1c2VyL0pvcnV1c19DX0Jhb3RoIiwiaWF0IjoxNzE0NjUzOTg0LCJleHAiOjE3MTUyNTg3ODl9.WkUHrFvbIdfjqKIcxi4FQB6GmQB1q0uyQEAfJ61P_g0",
                                loggingLevel = LoggingLevel(priority = Priority.VERBOSE),
                            )
                            .build()
                      }

                      val call = StreamVideo.instance().call("default", uid)
                      composeTestRule.setContent { VideoCallScreen(call) { call.leave() } }
                    }

                    @Test
                    fun elementsAreDisplayed() {
                      ComposeScreen.onComposeScreen<VideoCallScreen>(composeTestRule) {

                        runBlocking {
                          delay(6000) // Adjust the delay time as needed
                        }
                        callContent { assertIsDisplayed() }
                        controls { assertIsDisplayed() }


                      }
                    }

                    @After
                    fun afterTest() {
                      StreamVideo.removeClient()
                    }

                         */
