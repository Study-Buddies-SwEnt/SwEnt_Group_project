package com.github.se.studybuddies.tests

/*
@RunWith(AndroidJUnit4::class)
class MapTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
    @get:Rule
    val composeTestRule = createComposeRule()

    // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
    @get:Rule
    val mockkRule = MockKRule(this)

    // Relaxed mocks methods have a default implementation returning values
    @RelaxedMockK
    lateinit var mockNavActions: NavigationActions

    @RelaxedMockK private lateinit var mockContext: Context


    lateinit var mapScreen : MapScreen

    @RelaxedMockK private lateinit var mockLauncher: ManagedActivityResultLauncher<String, Boolean>

    private fun granted() {
        assert(true)
    }

    @Before
    fun setup() {
        mapScreen = MapScreen(composeTestRule)
        composeTestRule.setContent { mapScreen }
    }


    @Test
    fun mapIconIsDisplayed() {
        onComposeScreen<MapScreen>(composeTestRule) {
            mapIcon {
                assertIsDisplayed()
            }
        }
    }
    @Test
    fun permissionsRequestNotLaunched() {
        val permission = "android.permission.CALENDAR"
        mockLauncher =
            mockk<ManagedActivityResultLauncher<String, Boolean>>(relaxed = true) { launch(permission) }
        mockContext = mockk<Context>(relaxed = true) {}

        checkPermission(mockContext, permission, mockLauncher)
        assert(
            ContextCompat.checkSelfPermission(mockContext, permission) ==
                    PackageManager.PERMISSION_GRANTED)
        checkPermission(mockContext, permission, mockLauncher) { granted() }
        verify(exactly = 1) { mockLauncher.launch(permission) }
    }


}*/
