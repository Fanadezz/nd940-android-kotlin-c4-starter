package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminders: MutableList<ReminderDTO>? = mutableListOf()) : ReminderDataSource {
private var shouldReturnError = false





    override suspend fun saveReminder(reminder: ReminderDTO) {

        reminders?.add(reminder)

    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        /*wrap with an if-statement such that if shouldReturnError is true
       *  the method returns result.error*/

        if (shouldReturnError){

            return Result.Error("Test Exception")
        }

        //IF-NOT-NULL
        reminders?.let {

            return Result.Success(it.single { reminder -> reminder.id == id })

        }

        //IF-NULL
        return Result.Error("No reminder(s) found")
    }
    override suspend fun getReminders(): Result<List<ReminderDTO>> {

        /*wrap with an if-statement such that if shouldReturnError is true
        *  the method returns result.error*/

        if (shouldReturnError){

            return Result.Error("Test Exception")
        }

        //if reminders are not null, then return success result
        reminders?.let {

            return Result.Success(ArrayList(it))
        }

        //if list is null return error
        return Result.Error("No reminder(s) found")
    }


    override suspend fun deleteAllReminders() {

        reminders?.clear()
    }


fun shouldReturnError(value: Boolean){

    shouldReturnError = value
}


}