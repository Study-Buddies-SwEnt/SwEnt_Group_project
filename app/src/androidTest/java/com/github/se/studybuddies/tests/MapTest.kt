package com.github.se.studybuddies.tests

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.core.content.ContextCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.screens.MapScreen
import com.github.se.studybuddies.ui.permissions.checkPermission
import com.google.common.base.Verify.verify
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


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


}

