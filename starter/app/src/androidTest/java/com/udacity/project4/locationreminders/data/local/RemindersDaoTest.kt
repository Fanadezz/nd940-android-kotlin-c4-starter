package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.google.android.gms.tasks.Task
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

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
    fun insertReminder_getBReminderById() = mainCoroutineRule.runBlockingTest {

        //GIVEN - insert reminder
        val reminder = ReminderDTO(
            title = "Reminder",
            description = "Description",
            location = "Loc",
            latitude = 0.0,
            longitude = 0.0,

        )


        database.reminderDao().saveReminder(reminder)

        //WHEN - get reminder by id from the database
        val loadedReminder = database.reminderDao().getReminderById(reminder.id)


        //THEN - the loaded data matches/contains the inserted reminder
        assertThat(loadedReminder as ReminderDTO, notNullValue()) // assert non-null task came back
        assertThat(loadedReminder.id, `is`(loadedReminder.id))
        assertThat(loadedReminder.title, `is`(loadedReminder.title))
        assertThat(loadedReminder.description, `is`(loadedReminder.description))
        assertThat(loadedReminder.location, `is`(loadedReminder.location))
        assertThat(loadedReminder.latitude, `is`(loadedReminder.location))
        assertThat(loadedReminder.longitude ,`is`(loadedReminder.longitude))
    }


}