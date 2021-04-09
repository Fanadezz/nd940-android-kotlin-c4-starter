package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.filters.SmallTest
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*
import org.junit.runner.RunWith
import org.koin.test.AutoCloseKoinTest
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@SmallTest
class RemindersLocalRepositoryTest: AutoCloseKoinTest() {

    //    TODO: Add testing implementation to the RemindersLocalRepository.kt

    /*Executes each task synchronously using Architecture Components*/
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    //subjects under test
    private lateinit var localDataSource: ReminderDataSource
    private lateinit var database: RemindersDatabase


    @Before
    fun initDatabase() {

        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(), RemindersDatabase::class
                .java
        )
                .allowMainThreadQueries()
                .build()

        localDataSource = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
    }


    @After
    fun closeDatabase() {

        database.close()
    }


    @Test
    fun saveReminder_retrievesSameReminder() = mainCoroutineRule.runBlockingTest {

        //GIVEN - A new reminder saved in the database
        val reminder = ReminderDTO(
                title = "Title",
                description = "Description",
                location = "Loc",
                latitude = 0.0,
                longitude = 0.0
        )

        localDataSource.saveReminder(reminder)


        //WHEN - Reminder is retrieved by ID
        val result = localDataSource.getReminder(reminder.id)


        //THEN - The same reminder is returned
        result as Result.Success
        Assert.assertThat(result.data.title, `is`("Title"))
        Assert.assertThat(result.data.description, `is`("Description"))
        assertThat(result.data.location, `is`("Loc"))
        assertThat(result.data.latitude, `is`(0.0))
        assertThat(result.data.longitude, `is`(0.0))
    }

    @Test
    fun getReminderByInvalidId_returnNotFound() = mainCoroutineRule.runBlockingTest {

        //GIVEN - A new reminder saved in the database
        val reminder = ReminderDTO(
                title = "Title",
                description = "Description",
                location = "Loc",
                latitude = 0.0,
                longitude = 0.0
        )

        localDataSource.saveReminder(reminder)

        //WHEN -delete all reminders
        localDataSource.deleteAllReminders()

        //THEN - return error
        val errorMessage = (localDataSource.getReminder(reminder.id) as Result.Error).message

        assertThat(errorMessage, `is`("Reminder not found!"))
    }


}