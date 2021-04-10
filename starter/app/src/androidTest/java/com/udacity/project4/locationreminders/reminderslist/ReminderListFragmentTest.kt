package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify


@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@MediumTest

class ReminderListFragmentTest :
        AutoCloseKoinTest() { // Extended Koin Test - embed autoclose @after method to close Koin after every test

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {
        stopKoin() //stop the original app koin
        appContext = getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                        appContext,
                        get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                        appContext,
                        get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }
    }




    //TEST THE NAVIGATION OF THE FRAGMENTS.
    @Test
    fun clickFabButton_navigateToSelectLocation() {

        //GIVEN - ReminderListFragment
        val fragmentScenario = launchFragmentInContainer<ReminderListFragment>(
                Bundle(),
                R.style.AppTheme
        )
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


    //TESTING THE DISPLAYED DATA ON THE UI.
    @Test
    fun insertReminder_reminderDetailsDisplayed() = mainCoroutineRule.runBlockingTest {

        //GIVEN - a reminder
        val reminder = ReminderDTO(
                title = "Title",
                description = "Description",
                location = "Loc",
                latitude = 0.0,
                longitude = 0.0,
        )
        //WHEN - added to the Repository

        runBlocking {
            repository.apply {
                deleteAllReminders()
                saveReminder(reminder)
            }

        }

        //THEN - correct title and description are displayed on reminders list

        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        onView(withText("Title")).check(matches(isDisplayed()))
        onView(withText("Description")).check(matches(isDisplayed()))
        onView(withText("Loc")).check(matches(isDisplayed()))


    }




    //TESTING FOR THE ERROR MESSAGES.

    @Test
    fun emptyReminders_displayNoDataTextView() = mainCoroutineRule.runBlockingTest {


        //GIVEN - empty reminders
        runBlocking {
            repository.apply {

                deleteAllReminders()
            }

        }
        //WHEN - launch fragment
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)


        //THEN - noDataTextView is displayed
        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))

    }
}