package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.google.android.gms.tasks.Task
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {


    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun init() {

        database =
            Room.inMemoryDatabaseBuilder(getApplicationContext(), RemindersDatabase::class.java)
                .allowMainThreadQueries().build()
    }

    //Tear down database
    @After
    fun tearDownDatabase() {

        database.close()
    }
    @Test
    fun saveReminder_getReminderById() = mainCoroutineRule.runBlockingTest {

        //GIVEN - insert reminder
        val reminder = ReminderDTO(
            title = "Reminder",
            description = "Description",
            location = "Loc",
            latitude = 0.0,
            longitude = 0.0,

        )
        database.reminderDao().saveReminder(reminder)

        //WHEN - retrieving the reminder by Id from the database
        val loadedReminder = database.reminderDao().getReminderById(reminder.id)

        //THEN - the loaded data matches/contains the inserted reminder
        assertThat(loadedReminder as ReminderDTO, notNullValue()) // assert non-null task came back
        assertThat(loadedReminder.id, `is`(loadedReminder.id))
        assertThat(loadedReminder.title, `is`(loadedReminder.title))
        assertThat(loadedReminder.description, `is`(loadedReminder.description))
        assertThat(loadedReminder.location, `is`(loadedReminder.location))
        assertThat(loadedReminder.latitude, `is`(loadedReminder.latitude))
        assertThat(loadedReminder.longitude ,`is`(loadedReminder.longitude))
    }
    @Test
    fun getReminderByInvalidId_returnNull() = mainCoroutineRule.runBlockingTest {
        // GIVEN - a randomly generated reminder Id
        val randomId = UUID.randomUUID()
                .toString()
        // WHEN - retrieving a reminder using the random Id
        val loadedResult = database.reminderDao()
                .getReminderById(randomId)

        // THEN - returned result is null
        Assert.assertNull(loadedResult)

    }
}