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
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ProfileFragmentTest {
    private lateinit var profileScenario: FragmentScenario<ProfileFragment>
    private lateinit var navController: NavController

    @Before
    fun setUp() {
        profileScenario = launchFragmentInContainer(themeResId = R.style.Theme_6002CEM)
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        UiThreadStatement.runOnUiThread {
            profileScenario.onFragment {
                Navigation.setViewNavController(it.requireView(), navController)
            }
        }
    }

    @Test
    fun useAppContext() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        Assert.assertEquals("com.example.a6002cem", context.packageName)
    }
}