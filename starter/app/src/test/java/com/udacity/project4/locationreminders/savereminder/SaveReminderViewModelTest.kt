package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@SmallTest
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.P])
class SaveReminderViewModelTest {

    //date source
    private lateinit var dataSource: FakeDataSource

    //subject under test
    private lateinit var viewModel: SaveReminderViewModel

    /*This rule runs architecture components related background jobs in the same thread
    * This ensures that the test result happen synchronously and in repeatable order*/
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()


    @Before
    fun init() {

        //swap data source
        dataSource = FakeDataSource()

        //initialize viewModel
        viewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), dataSource)

    }


    @Test
    fun onClear_reminderNull() {
        //GIVEN - onClear() called
        viewModel.onClear()

        //WHEN - testing LiveData

        //THEN - LiveData is null
        assertThat(viewModel.reminderTitle.getOrAwaitValue(), `is`(nullValue()))
        assertThat(viewModel.reminderDescription.getOrAwaitValue(), `is`(nullValue()))
        assertThat(viewModel.reminderSelectedLocationStr.getOrAwaitValue(), `is`(nullValue()))
        assertThat(viewModel.selectedPOI.getOrAwaitValue(), `is`(nullValue()))
        assertThat(viewModel.latitude.getOrAwaitValue(), `is`(nullValue()))
        assertThat(viewModel.longitude.getOrAwaitValue(), `is`(nullValue()))
    }

    @Test
    fun validateEnteredData_whenTitleIsEmpty_returnFalse() {

        //GIVEN - a reminder to validate
        val reminder = ReminderDataItem("Title", "Description", "Loc", 0.0, 0.0)

        //WHEN - Title is empty
        reminder.title = ""

        //THEN - return false
        assertThat(viewModel.validateEnteredData(reminder), `is`(false))

    }

    @Test
    fun validateEnteredData_whenLocationIsEmpty_returnFalse() {

        //GIVEN - a reminder to validate
        val reminder = ReminderDataItem("Title", "Description", "Loc", 0.0, 0.0)

        //WHEN - Location is null
        reminder.location = ""

        //THEN - return false
        assertThat(viewModel.validateEnteredData(reminder), `is`(false))

    }

    @Test
    fun titleNull_showSnackBar() {

        //GIVEN - a reminder to validate
        val reminder = ReminderDataItem("Title", "Description", "Loc", 0.0, 0.0)

        //WHEN - Title is null
        reminder.title = null

        viewModel.validateAndSaveReminder(reminder)
        //THEN - snackbar is shown
        assertThat(viewModel.showSnackBarInt.getOrAwaitValue(), `is`("Please enter title"))
    }


    @Test
    fun saveReminder_showLoading() = runBlockingTest {

        //GIVEN - a reminder to save
        val reminder = ReminderDataItem("Title", "Description", "Loc", 0.0, 0.0)
        mainCoroutineRule.pauseDispatcher()


        //WHEN - saving reminder
        viewModel.saveReminder(reminder)

        //THEN - show loading snackbar
        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(true))
        mainCoroutineRule.resumeDispatcher()
        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(false))

    }

}