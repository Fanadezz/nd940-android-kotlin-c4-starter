package com.udacity.project4.locationreminders.savereminder

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.BuildConfig
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSaveReminderBinding
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.savereminder.SaveReminderFragment.Companion.ACTION_GEOFENCE_EVENT
import com.udacity.project4.locationreminders.savereminder.selectreminderlocation.SelectLocationFragment
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import kotlinx.android.synthetic.main.it_reminder.*
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.concurrent.TimeUnit

class SaveReminderFragment : BaseFragment() {
    //Get the view model this time as a single to be shared with the another fragment
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSaveReminderBinding
    private lateinit var geofencingClient: GeofencingClient


    private val pendingIntent: PendingIntent by lazy {

        val intent = Intent(activity, GeofenceBroadcastReceiver::class.java)
        intent.action = ACTION_GEOFENCE_EVENT
        PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    companion object {

        const val ACTION_GEOFENCE_EVENT = "SelectLocationFragment.ACTION_GEOFENCE_EVENT"
        const val RADIUS_IN_METRES = 100f
        val EXPIRY_TIME: Long = TimeUnit.HOURS.toMillis(1)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_save_reminder, container, false)

        setDisplayHomeAsUpEnabled(true)

        binding.viewModel = _viewModel



        //initialize GeofencingClient
        geofencingClient = LocationServices.getGeofencingClient(requireActivity())
        Timber.i("onCreateView() for SaveReminderFragment Called")
        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.selectLocation.setOnClickListener {
            //            Navigate to another fragment to get the user location
            _viewModel.navigationCommand.value =
                    NavigationCommand.To(
                            SaveReminderFragmentDirections.actionSaveReminderFragmentToSelectLocationFragment())
        }


        //set setOnClickListener to FAB
        binding.saveReminder.setOnClickListener {
            val title = _viewModel.reminderTitle.value
            val description = _viewModel.reminderDescription.value
            val location = _viewModel.reminderSelectedLocationStr.value
            val latitude = _viewModel.latitude.value
            val longitude = _viewModel.longitude.value

            val reminder = ReminderDataItem(
                    title = title,
                    description = description,
                    latitude = latitude,
                    longitude = longitude,
                    location = location
                )
            _viewModel.validateAndSaveReminder(reminder)
//Null Check
if (reminder.longitude!=null && reminder.latitude!=null){
    addGeofence(reminder)
}






//            TODO: use the user entered reminder details to:
//             1) add a geofencing request
//             2) save the reminder to the local db




        }
    }


    @SuppressLint("MissingPermission")
    private fun addGeofence(reminder: ReminderDataItem){


        //build geofence object
            val geofence = Geofence.Builder()
                    .setRequestId(reminder.id)
                    .setCircularRegion(reminder.latitude!!,
                                       reminder.longitude!!,
                                       RADIUS_IN_METRES)
                    .setExpirationDuration(EXPIRY_TIME)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .build()

            //build a geofencing request

            val geofencingRequest = GeofencingRequest.Builder()
                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                    .addGeofence(geofence)
                    .build()

            geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                    .addOnSuccessListener {

                        Timber.i("Geofence added successfully")
                    }
                    .addOnFailureListener {
                        Timber.i("Geofence Addition Failed: $it")
                    }
        }

    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClear()
    }



    }
