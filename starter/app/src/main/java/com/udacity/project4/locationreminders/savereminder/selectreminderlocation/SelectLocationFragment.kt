package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.res.Resources
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import kotlinx.android.synthetic.main.fragment_select_location.*
import org.koin.android.ext.android.inject
import timber.log.Timber

class SelectLocationFragment : BaseFragment() {

    //vars
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var lastKnownLocation: Location
    private lateinit var selectedPOI: PointOfInterest
    private lateinit var poiLatLng: LatLng
    private lateinit var poiName: String
    private var poiIsInitialized = false
    private val runningQOrLater = Build.VERSION.SDK_INT >= Build
            .VERSION_CODES.Q

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container,
                                          false)

        if (runningQOrLater) {

            //request for permission using Activity Result API
            permissionCheckLauncher.launch(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                                                   Manifest.permission.ACCESS_FINE_LOCATION))
        }else{

            //request for permission using Activity Result API
            permissionCheckLauncher.launch(arrayOf(
                                                   Manifest.permission.ACCESS_FINE_LOCATION))
        }




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
        }
//        TODO: call this function after the user confirms on the selected location


        return binding.root
    }

    private val onMapReadyCallback = OnMapReadyCallback {
        //initialize map
        map = it


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(onMapReadyCallback)


    }


    @SuppressLint("MissingPermission")
    private fun moveCameraAndAddMarker(location: Location) {
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

    //suppress permission check
    @SuppressLint("MissingPermission")
    private val permissionCheckLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {

                permissions ->

                if (permissions[Manifest.permission.ACCESS_BACKGROUND_LOCATION] == true &&
                        permissions[Manifest.permission.ACCESS_FINE_LOCATION]== true) {

                    getLastKnownLocation()
                    Timber.i("Permissions Granted")
                } else {

                    Timber.i("Permissions Denied")
                }


            }


    @SuppressLint("MissingPermission")
    fun getLastKnownLocation() {

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                lastKnownLocation = location
                moveCameraAndAddMarker(lastKnownLocation)
            }
        }
                .addOnFailureListener {

                    makeText(requireActivity(), "${it.message}", Toast.LENGTH_LONG)
                            .show()
                }
    }

    private fun onLocationSelected() {
        //        TODO: When the user confirms on the selected location,
        //         send back the selected location details to the view model
        //         and navigate back to the previous fragment to save the reminder and add the geofence
        if (poiIsInitialized) {
            findNavController().navigate(SelectLocationFragmentDirections
                                                 .actionSelectLocationFragmentToSaveReminderFragment())

            //set the poi value in the shared viewModel
            _viewModel.selectedPOI.value = selectedPOI

            //set latitude, longitude and location string
            _viewModel.latitude.value = poiLatLng.latitude
            _viewModel.longitude.value = poiLatLng.longitude
            _viewModel.reminderSelectedLocationStr.value = poiName


        } else {
            Snackbar.make(binding.root, getString(R.string.pick_point_of_interest_msg),
                          Snackbar.LENGTH_SHORT)
                    .show()
        }

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


}
