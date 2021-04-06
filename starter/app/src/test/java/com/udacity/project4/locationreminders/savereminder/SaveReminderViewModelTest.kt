package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@SmallTest
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
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

    @After
    fun tearDown() {
        stopKoin()
    }


}