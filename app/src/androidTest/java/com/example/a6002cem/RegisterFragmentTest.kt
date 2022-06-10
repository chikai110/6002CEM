package com.example.a6002cem

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterFragmentTest {
    private lateinit var registerScenario: FragmentScenario<RegisterFragment>
    private lateinit var navController: NavController

    @Before
    fun setUp() {
        registerScenario = launchFragmentInContainer(themeResId = R.style.Theme_6002CEM)
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        UiThreadStatement.runOnUiThread {
            registerScenario.onFragment {
                Navigation.setViewNavController(it.requireView(), navController)
            }
        }
    }

    @Test
    fun useAppContext() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        Assert.assertEquals("com.example.a6002cem", context.packageName)
    }

    @Test
    fun userCanEnterUsername() {
        insertInUsernameEditText("chikai0110@gmail.com")
    }

    @Test
    fun userCanEnterPassword() {
        insertInPwdEditText("dh239048fy")
    }

    @Test
    fun userCanClickSignUpText() {
        clickSignUpText()
    }

    @Test
    fun userCanClickLoginButton() {
        clickLoginButton()
    }

    @Test
    fun onLogin_emptyForm_showsError() {
        clickLoginButton()

        Espresso.onView(ViewMatchers.withId(R.id.login_error_text_view))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun onLogin_invalidUsername_showsError() {
        insertInUsernameEditText("  467856 ")
        insertInPwdEditText("fd3g24")
        clickLoginButton()

        Espresso.onView(ViewMatchers.withId(R.id.login_error_text_view))
            .check(ViewAssertions.matches(ViewMatchers.hasErrorText(CoreMatchers.`is`("Login Fail"))))
    }

    @Test
    fun onLogin_validData_showsNoError() {
        Intents.init()

        insertInUsernameEditText("chikai0110@gmail.com")
        insertInPwdEditText("1234")
        clickLoginButton()

        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
    }

    private fun insertInUsernameEditText(username: String) =
        Espresso.onView(ViewMatchers.withId(R.id.log_username)).perform(
            ViewActions.scrollTo(),
            ViewActions.clearText(),
            ViewActions.typeText(username)
        )

    private fun insertInPwdEditText(pwd: String) =
        Espresso.onView(ViewMatchers.withId(R.id.log_password)).perform(
            ViewActions.scrollTo(),
            ViewActions.clearText(),
            ViewActions.typeText(pwd)
        )

    private fun clickLoginButton() =
        Espresso.onView(ViewMatchers.withId(R.id.btn_login))
            .perform(ViewActions.scrollTo(), ViewActions.click())

    private fun clickSignUpText() =
        Espresso.onView(ViewMatchers.withId(R.id.btn_register)).perform(
            ViewActions.scrollTo(),
            clickClickableSpan("New User?")
        )
}