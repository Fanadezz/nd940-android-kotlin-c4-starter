package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminders: MutableList<ReminderDTO>? = mutableListOf()) :
    ReminderDataSource {


//    TODO: Create a fake data source to act as a double to the real data source

    override suspend fun getReminders(): Result<List<ReminderDTO>> {

        //if reminders are not null, then return success result
        reminders?.let {

            return Result.Success(ArrayList(it))
        }

        //if list is null return error
        return Result.Error("No reminder(s) found")
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        // TODO("save the reminder")
        reminders?.add(reminder)

    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        //  TODO("return the reminder with the id")

        reminders?.let {

            return Result.Success(it.single { reminder -> reminder.id == id })

        }

        return Result.Error("No reminder(s) found")
    }

    override suspend fun deleteAllReminders() {
        //  TODO("delete all the reminders")
        reminders?.clear()
    }





}