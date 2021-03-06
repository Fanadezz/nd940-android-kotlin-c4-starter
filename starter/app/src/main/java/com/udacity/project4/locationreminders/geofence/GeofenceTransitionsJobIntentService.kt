package com.udacity.project4.locationreminders.geofence

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.savereminder.SaveReminderFragment
import com.udacity.project4.utils.sendNotification
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class GeofenceTransitionsJobIntentService : JobIntentService(), CoroutineScope {

    private var coroutineJob: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineJob

    companion object {
        private const val JOB_ID = 573


        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(
                    context,
                    GeofenceTransitionsJobIntentService::class.java, JOB_ID,
                    intent
            )
        }
    }

    override fun onHandleWork(intent: Intent) {

        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (intent.action == SaveReminderFragment.ACTION_GEOFENCE_EVENT) {

            //check for errors on the GeofencingEvent
            if (geofencingEvent.hasError()) {
                val error = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
                Timber.i("Error: $error")

                return
            }
            if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

                Timber.i("Transition_enter_found")

                sendNotification(geofencingEvent.triggeringGeofences)
            }
        }


    }


    private fun sendNotification(triggeringGeofences: List<Geofence>) {
        val requestId = when {

            triggeringGeofences.isNotEmpty() -> {
                triggeringGeofences[0].requestId
            }
            else -> return
        }
        Timber.i("Request Id is $requestId")
        //Get the local repository instance
        val remindersLocalRepository: ReminderDataSource by inject()
        //        Interaction to the repository has to be through a coroutine scope
        CoroutineScope(coroutineContext).launch(SupervisorJob()) {

            Timber.i("Entering Coroutine Scope")
            //get the reminder with the request id
            val result = remindersLocalRepository.getReminder(requestId)
            if (result is Result.Success<ReminderDTO>) {
                val reminderDTO = result.data
                //send a notification to the user with the reminder details
                sendNotification(
                        this@GeofenceTransitionsJobIntentService, ReminderDataItem(
                        reminderDTO.title,
                        reminderDTO.description,
                        reminderDTO.location,
                        reminderDTO.latitude,
                        reminderDTO.longitude,
                        reminderDTO.id
                )
                )

                Timber.i("Send Notification called")
            }
        }
    }

}