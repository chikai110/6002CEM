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
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasErrorText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.gms.internal.phenotype.zzh.init
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginFragmentTest {
    private lateinit var loginScenario: FragmentScenario<LoginFragment>
    private lateinit var navController: NavController

    @Before
    fun setUp() {
        loginScenario = launchFragmentInContainer(themeResId = R.style.Theme_6002CEM)
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        UiThreadStatement.runOnUiThread {
            loginScenario.onFragment {
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

        Espresso.onView(withId(R.id.login_error_text_view))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun onLogin_invalidUsername_showsError() {
        insertInUsernameEditText("  467856 ")
        insertInPwdEditText("fd3g24")
        clickLoginButton()

        Espresso.onView(withId(R.id.login_error_text_view))
            .check(ViewAssertions.matches(hasErrorText(`is`("Login Fail"))))
    }

    @Test
    fun onLogin_validData_showsNoError() {
        Intents.init()

        insertInUsernameEditText("chikai0110@gmail.com")
        insertInPwdEditText("1234")
        clickLoginButton()

        intended(hasComponent(MainActivity::class.java.name))
    }

    private fun insertInUsernameEditText(username: String) =
        Espresso.onView(withId(R.id.log_username)).perform(
            ViewActions.scrollTo(),
            ViewActions.clearText(),
            ViewActions.typeText(username)
        )

    private fun insertInPwdEditText(pwd: String) =
        Espresso.onView(withId(R.id.log_password)).perform(
            ViewActions.scrollTo(),
            ViewActions.clearText(),
            ViewActions.typeText(pwd)
        )

    private fun clickLoginButton() =
        Espresso.onView(withId(R.id.btn_login))
            .perform(ViewActions.scrollTo(), ViewActions.click())

    private fun clickSignUpText() =
        Espresso.onView(withId(R.id.btn_register)).perform(
            ViewActions.scrollTo(),
            clickClickableSpan("New User?")
        )
}