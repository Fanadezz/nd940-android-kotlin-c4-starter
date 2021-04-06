package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config


@SmallTest
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.P])
class RemindersListViewModelTest {

    //date source
    private lateinit var dataSource: FakeDataSource

    //subject under test
    private lateinit var viewModel: RemindersListViewModel

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
        viewModel = RemindersListViewModel(getApplicationContext(), dataSource)

    }

    @After
    fun tearDown() {
        stopKoin()
    }


    @Test
    fun addReminder_reminderListNotEmpty() = mainCoroutineRule.runBlockingTest {

        //GIVEN  - list of empty reminders
        dataSource.deleteAllReminders()
        //WHEN - add a reminder
        val reminder = ReminderDTO("Title", "Desc", "Loc", 0.0, 0.0)
        dataSource.saveReminder(reminder)
        viewModel.loadReminders()
        //THEN - reminder list is not empty
        assertThat(viewModel.remindersList.getOrAwaitValue().isNotEmpty(), `is`(true))
    }

    @Test
    fun deleteAllReminders_remindersListEmpty() = mainCoroutineRule.runBlockingTest {

        //delete data
        dataSource.deleteAllReminders()
        //load reminders
        viewModel.loadReminders()


        assertThat(viewModel.remindersList.getOrAwaitValue().size, `is`(0))

    }

    @Test
    fun shouldReturnError_snackbarShowsException() = runBlockingTest {
        //Make the repository return errors
        dataSource.shouldReturnError(true)
        viewModel.loadReminders()

//Then assert that an error is displayed
        assertThat(
            ((viewModel.showSnackBar) as LiveData<*>).getOrAwaitValue() == "Test Exception",
            `is`(true)
        )

    }



    //TODO: provide testing to the RemindersListViewModel and its live data objects


}