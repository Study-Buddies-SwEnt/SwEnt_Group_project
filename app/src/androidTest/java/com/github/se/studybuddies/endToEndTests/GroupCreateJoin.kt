package com.github.se.studybuddies.endToEndTests

/*
@RunWith(AndroidJUnit4::class)
class GroupCreateJoin : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

    @get:Rule val composeTestRule = createComposeRule()
  @get:Rule val mockkRule = MockKRule(this)

  lateinit var navigationActions: NavigationActions

  // Use the userTest created manually in the database
  private val uid = "userTestE2E"
  private val userTest =
      User(
          uid = uid,
          email = "test@gmail.com",
          username = "testUser",
          photoUrl =
              Uri.parse("https://images.pexels.com/photos/6031345/pexels-photo-6031345.jpeg"),
          location = "offline",
          dailyPlanners = emptyList())
  val db: DbRepository = MockDatabase()
  private val userVM = UserViewModel(uid, db)
  private val groupHomeVM = GroupsHomeViewModel(uid, db)
  private val groupVM = GroupViewModel(uid, db)

  @Before
  fun testSetup() {
    composeTestRule.setContent {
      StudyBuddiesTheme {
        val navController = rememberNavController()
        navigationActions = NavigationActions(navController)
        val startDestination = Route.CREATEACCOUNT
        NavHost(navController = navController, startDestination = startDestination) {
          /*composable(Route.START) {
            if (auth.currentUser != null) {
              db.userExists(
                  uid = db.getCurrentUserUID(),
                  onSuccess = { userExists ->
                    if (userExists) {
                      navController.navigate(Route.SOLOSTUDYHOME)
                    } else {
                      navController.navigate(Route.CREATEACCOUNT)
                    }
                  },
                  onFailure = { navController.navigate(Route.SOLOSTUDYHOME) })
            } else {
              navController.navigate(Route.CREATEACCOUNT)
            }
          }*/
          composable(Route.LOGIN) { LoginScreen(navigationActions) }
          composable(Route.CREATEACCOUNT) { CreateAccount(userVM, navigationActions) }
          composable(Route.SOLOSTUDYHOME) { SoloStudyHome(navigationActions) }
          composable(
              route = "${Route.SETTINGS}/{backRoute}",
              arguments = listOf(navArgument("backRoute") { type = NavType.StringType })) {
                  backStackEntry ->
                val backRoute = backStackEntry.arguments?.getString("backRoute")
                if (backRoute != null) {
                  Settings(backRoute, navigationActions)
                }
              }
          composable(
              route = "${Route.ACCOUNT}/{backRoute}",
              arguments = listOf(navArgument("backRoute") { type = NavType.StringType })) {
                  backStackEntry ->
                val backRoute = backStackEntry.arguments?.getString("backRoute")
                if (backRoute != null) {
                  AccountSettings(uid, userVM, backRoute, navigationActions)
                }
              }
          composable(Route.GROUPSHOME) { GroupsHome(uid, groupHomeVM, navigationActions, db) }
          composable(Route.CREATEGROUP) { CreateGroup(groupVM, navigationActions) }
        }
      }
    }
  }

  @Test
  fun groupCreateJoin() {
    ComposeScreen.onComposeScreen<CreateAccountScreen>(composeTestRule) {
      // Create account
      // saveButton { assertIsNotEnabled() }
      usernameField {
        performTextClearance()
        performTextInput("UserTestE2E")
        assertTextContains("UserTestE2E")
      }
      Espresso.closeSoftKeyboard()
      // saveButton { performClick() }
    } /*
      ComposeScreen.onComposeScreen<SoloStudyScreen>(composeTestRule) {
        soloStudyScreen { assertIsDisplayed() }
        // groupsBottom { performClick() }
      }
        ComposeScreen.onComposeScreen<GroupsHomeScreen>(composeTestRule) {
          groupScreen { assertIsDisplayed() }
          addButton { performClick() }
        }
        ComposeScreen.onComposeScreen<CreateGroupScreen>(composeTestRule) {
          // Create a group
          groupField {
            performTextClearance()
            performTextInput("testGroup")
            assertTextContains("testGroup")
          }
          Espresso.closeSoftKeyboard()
          // saveButton { performClick() }
          goBackButton { performClick() }
        }
        ComposeScreen.onComposeScreen<GroupsHomeScreen>(composeTestRule) {
          drawerMenuButton { performClick() }
          accountButton { performClick() }
        }
        ComposeScreen.onComposeScreen<AccountSettingsScreen>(composeTestRule) {
          signOutButton {
            assertIsEnabled()
            assertHasClickAction()
            performClick()
          }
        }
        ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) {
          // Verify that we indeed went back to the login screen
          loginTitle {
            assertIsDisplayed()
            assertTextEquals("Study Buddies")
          }
        }
           */
  }
}*/
