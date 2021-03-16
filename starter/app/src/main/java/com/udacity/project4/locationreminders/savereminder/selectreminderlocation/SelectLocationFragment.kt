package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.res.Resources
import android.location.Location

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.*
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.BuildConfig
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.concurrent.TimeUnit

class SelectLocationFragment : BaseFragment() {

    //vars
    private lateinit var map: GoogleMap
    private lateinit var lastKnownLocation: Location
    private lateinit var selectedPOI: PointOfInterest
    private lateinit var poiLatLng: LatLng
    private lateinit var poiName: String
    private var poiIsInitialized = false


    //Location Components

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    //create a location request object
    private val locationRequest = LocationRequest().apply {

        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
      interval = TimeUnit.HOURS.toMillis(1)

        fastestInterval = TimeUnit.MINUTES.toMillis(5)
    }

    private val locationCallback = object : LocationCallback() {

        @RequiresApi(Build.VERSION_CODES.Q)
        override fun onLocationResult(locatioResult: LocationResult?) {
            super.onLocationResult(locatioResult)

            if (locatioResult != null) {
                lastKnownLocation = locatioResult.lastLocation
                moveCameraAndAddMarker(lastKnownLocation)
                Timber.i("lastKnown Location initialized via call back")
            }

        }
    }


    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStart() {
        super.onStart()
        Timber.i("onStart() called")
        foregroundPermLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

   

    override fun onStop() {
        super.onStop()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private val onMapReadyCallback = OnMapReadyCallback {
        //initialize map
        map = it

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {


        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container,
                                          false)

        //initialize fusedLocationProviderClient
        fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

//        TODO: add the map setup implementation
//        TODO: zoom to the user location after taking his permission
//        TODO: add style to the map
//        TODO: put a marker to location that the user selected

        binding.buttonSave.setOnClickListener {

            onLocationSelected()
            backgroundPermLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
//        TODO: call this function after the user confirms on the selected location


        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(onMapReadyCallback)
    }

    //suppress permission check
    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingPermission")
    private val foregroundPermLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {

                isGranted ->

                if (isGranted) {
                    Timber.i("Permissions Granted")

                    getLastKnownLocation()

                } else {

                    Timber.i("Permissions Denied")
                    showRationale(Manifest.permission.ACCESS_FINE_LOCATION)

                }
            }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingPermission")
    fun getLastKnownLocation() {
        Timber.i("getting the last known location")
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                lastKnownLocation = location
                moveCameraAndAddMarker(lastKnownLocation)

                Timber.i("Location is not null, initialized inside if-block")
            } else {
                showLocationSettingDialog()
                Timber.i("Location is null, entering else block")
                fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                                                                   locationCallback,
                                                                   Looper.getMainLooper())


            }
        }
                .addOnFailureListener {

                    makeText(requireActivity(), "${it.message}", Toast.LENGTH_LONG)
                            .show()
                }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingPermission")
    private fun moveCameraAndAddMarker(location: Location) {

        Timber.i("Moving the camera")
        //clear any marker first
        map.clear()

        poiIsInitialized = false

        val latLng = LatLng(location.latitude, location.longitude)

        //add marker
        val myMarker = map.addMarker(MarkerOptions()
                                             .position(latLng)
                                             .title("My Location")
                                             .icon(BitmapDescriptorFactory.defaultMarker(
                                                     BitmapDescriptorFactory.HUE_GREEN)))

        myMarker.showInfoWindow()

        //move camera
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18F))
        //add my Location Button on top-right side corner
        map.isMyLocationEnabled = true

        //add map customization
        styleBaseMap(map)

        map.setOnPoiClickListener {

            //clear markers
            map.clear()

            //add poiMarker
            val poiMarker = map.addMarker(MarkerOptions().position(it.latLng)
                                                  .title(it.name)
                                                  .icon
                                                  (BitmapDescriptorFactory.defaultMarker(
                                                          BitmapDescriptorFactory.HUE_BLUE)))

            poiMarker.showInfoWindow()

            //set details for point of interest
            selectedPOI = PointOfInterest(it.latLng, it.placeId, it.name)

            //toggle the poi initialization tracker
            poiIsInitialized = true

            //set poiLatLng
            poiLatLng = it.latLng

            //set poi name
            poiName = it.name
        }

    }


    private fun onLocationSelected() {
        //        TODO: When the user confirms on the selected location,
        //         send back the selected location details to the view model
        //         and navigate back to the previous fragment to save the reminder and add the geofence
        if (poiIsInitialized) {


            //set the poi value in the shared viewModel
            _viewModel.selectedPOI.value = selectedPOI

            //set latitude, longitude and location string
            _viewModel.latitude.value = poiLatLng.latitude
            _viewModel.longitude.value = poiLatLng.longitude
            _viewModel.reminderSelectedLocationStr.value = poiName


            //Navigate back to SaveReminderFragment
            _viewModel.navigationCommand.value = NavigationCommand.Back

        } else {
            Snackbar.make(binding.root, getString(R.string.pick_point_of_interest_msg),
                          Snackbar.LENGTH_SHORT)
                    .show()
        }

    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private val backgroundPermLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()) {

        isGranted ->
        if (isGranted) {

            Timber.i("Background Permissions Granted!")
            getLastKnownLocation()
        } else {
            Timber.i("Background Perms Denied")

            showRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)

        }


    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkDeviceLocationSettings(resolve: Boolean = true) {

        Timber.i("checking location settings")
        //val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        //startActivity(intent)

        /*val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(requireActivity())


        val locationSettingsResponseTask = settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->

            if (exception is ResolvableApiException && resolve) {
                try {
Timber.i("trying to turn location on")
                    exception.startResolutionForResult(activity,
                                                       REQUEST_TURN_DEVICE_LOCATION_ON)
                } catch (sendEx: IntentSender.SendIntentException) {

                    Timber.i("Error getting location settings resolution: %s", sendEx.message)

                }
            } else {
                Timber.i("unable turn location on")
                Snackbar.make(binding.root, R.string.location_required_error,
                              Snackbar.LENGTH_INDEFINITE)
                        .setAction(android.R.string.ok) {

                            checkDeviceLocationSettings()
                        }
                        .show()
            }
        }
              locationSettingsResponseTask  .addOnCompleteListener {

                    if (it.isSuccessful) {


                        Timber.i("Resolution successful")

                    } else {
                        Timber.i("Resolution not successful")

                    }
                }*/
    }

    private fun showRationale(permission: String) {
        if (shouldShowRequestPermissionRationale(permission)) {
            val materialAlertDialogBuilder = MaterialAlertDialogBuilder(
                    requireActivity()).setTitle("Location Permission")
                    .setMessage(R.string.rationale_for_location_permissions)
                    .setPositiveButton(R.string.settings) { dialog, _ ->

                        startActivity(Intent().apply {

                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        })


                    }
                    .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                        Snackbar.make(binding.root, R.string.location_required_error,
                                      Snackbar.LENGTH_INDEFINITE)
                                .setAction(android.R.string.ok) {

                                    showSnackBar(
                                            getString(R.string.rationale_for_location_permissions))
                                }
                                .show()
                    }
                    .create()
            materialAlertDialogBuilder.show()
            Timber.i("showing rationale dialog")
        }
    }


    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, R.string.location_required_error,
                      Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.settings) {
                    startActivity(Intent().apply {

                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })

                }
                .show()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }


    //Change the map type based on the user's selection
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.normal_map -> {

            map.mapType = GoogleMap.MAP_TYPE_NORMAL

            true
        }
        R.id.hybrid_map -> {

            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {

            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }


    private fun styleBaseMap(map: GoogleMap) {
        try {
            //customize base map style

            val success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(activity, R.raw.map_style))
            if (!success) {

                Timber.i("Custom Style Parsing Failes")
            }
        } catch (e: Resources.NotFoundException) {

            Timber.i("Error: $e")
        }

    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun isLocationEnabled(): Boolean {

        val locationManager = requireActivity().getSystemService(LocationManager::class.java)
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showLocationSettingDialog() {

        if (!isLocationEnabled()) {

            val materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(R.string.enable_location)
                    .setMessage(R.string.enable_dialog_message)
                    .setPositiveButton(getString(R.string.settings)) { _, _ ->
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        startActivity(intent)
                    }.setNegativeButton(getString(R.string.exit)){_, _ ->
                        Snackbar.make(binding.root, R.string.location_required_error,Snackbar
                                .LENGTH_INDEFINITE)
                                .setAction(getString(android.R.string.ok)){}.show()
                    }
                    .create()
                    materialAlertDialogBuilder.show()
        }
    }
}

private const val REQUEST_TURN_DEVICE_LOCATION_ON = 10019


