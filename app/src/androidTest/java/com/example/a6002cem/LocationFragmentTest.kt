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
import org.junit.Before
import org.junit.Test

class LocationFragmentTest {
    private lateinit var locationScenario: FragmentScenario<LocationFragment>
    private lateinit var navController: NavController

    @Before
    fun setUp() {
        locationScenario = launchFragmentInContainer(themeResId = R.style.Theme_6002CEM)
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        UiThreadStatement.runOnUiThread {
            locationScenario.onFragment {
                Navigation.setViewNavController(it.requireView(), navController)
            }
        }
    }

    @Test
    fun userCanClickGPSButton() {
        clickGPSButton()

        Espresso.onView(ViewMatchers.withId(R.id.latitude_text))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun userCanClickLocateButton() {
        clickLocateButton()

        Espresso.onView(ViewMatchers.withId(R.id.current_location))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    private fun clickGPSButton() =
        Espresso.onView(ViewMatchers.withId(R.id.locateOnOff))
            .perform(ViewActions.scrollTo(), ViewActions.click())

    private fun clickLocateButton() =
        Espresso.onView(ViewMatchers.withId(R.id.locate))
            .perform(ViewActions.scrollTo(), ViewActions.click())
}