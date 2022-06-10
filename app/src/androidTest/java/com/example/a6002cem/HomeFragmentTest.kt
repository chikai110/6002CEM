package com.example.a6002cem

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeFragmentTest {
    private lateinit var homeScenario: FragmentScenario<HomeFragment>
    private lateinit var navController: NavController

    private val context = ApplicationProvider.getApplicationContext<Context>()

    private val Item = Item(
        "movieId_000",
        "0 mins",
        "https://storage.movie6.com/movie/3874a_62JurassicWorldDominion-HKmontage1sheet_1_copy_1652681930.jpg",
        "Test Info",
        "Hong Kong",
        "3.0",
        "1 Jan 1999",
        "Happy Movie"
    )

    @Test
    fun showItemsCardView() = runBlockingTest {

        //testing recyclerview items
        Espresso.onView(withId(R.id.recycler_view))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<ItemAdapter.ItemViewHolder>(
                    0,
                    object : RecyclerViewItemAction() {
                        override fun perform(uiController: UiController?, view: View) {
                            val title: ImageView = view.findViewById(R.id.item_title)
                            ViewMatchers.assertThat(title.isVisible, Matchers.`is`(false))
                        }
                    })
            )
    }


    @Test
    fun onSelectItem_openItemDetailsFragment() {
        onView(withId(R.id.item_title)).perform(click())
        onView(withText(R.string.item_details_like_btn_desc)).inRoot(isDialog())
            .check(matches(isDisplayed())).perform(click())

        assertThat(navController.currentDestination?.id, `is`(R.id.item_details_like_btn))
    }

    private fun setScenarioAndNav() {
        homeScenario = launchFragmentInContainer(Bundle(), R.style.Theme_6002CEM)
        navController = TestNavHostController(context)
        UiThreadStatement.runOnUiThread {
            homeScenario.onFragment {
                Navigation.setViewNavController(it.requireView(), navController)
            }
        }
    }

}