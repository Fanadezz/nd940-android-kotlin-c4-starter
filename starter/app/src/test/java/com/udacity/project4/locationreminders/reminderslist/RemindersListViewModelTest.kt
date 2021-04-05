package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.ReminderDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //date source
    private lateinit var dataSource: FakeDataSource

    //subject under test
    private lateinit var viewModel:RemindersListViewModel

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()


    @Before
    fun init(){

        //swap data source
        dataSource = FakeDataSource()

        //initialize viewModel
        viewModel = RemindersListViewModel(getApplicationContext(), dataSource)

    }

@Test
fun deleteAllReminders_remindersListEmpty() = mainCoroutineRule.runBlockingTest{

    //delete data
    dataSource.deleteAllReminders()

    assertThat()
}

    //TODO: provide testing to the RemindersListViewModel and its live data objects

}