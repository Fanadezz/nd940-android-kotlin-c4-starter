package com.udacity.project4.locationreminders.reminderslist

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {




    //    TODO: test the navigation of the fragments.

    //Test the navigation of the fragments.
    @Test
    fun clickFabButton_navigateToSelectLocation() {

        //GIVEN - ReminderListFragment
        val fragmentScenario = launchFragmentInContainer<ReminderListFragment>(Bundle(),
                                                                               R.style.AppTheme)
        val navController = mock(NavController::class.java)

        fragmentScenario.onFragment {

            //here we can call methods on the fragment itself
            Navigation.setViewNavController(it.view!!, navController)
        }

        //WHEN - clicking FAB button
        onView(withId(R.id.addReminderFAB)).perform(click())

        //THEN - navigates to SaveReminderFragment
verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())
    }
    //    TODO: test the displayed data on the UI.

    @Test
    fun putText(){




    }
    //    TODO: add testing for the error messages.
}