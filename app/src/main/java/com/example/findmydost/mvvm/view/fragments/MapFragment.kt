package com.example.findmydost.mvvm.view.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.animation.Animation

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.findmydost.R
import com.example.findmydost.databinding.FragmentMapBinding
import com.example.findmydost.mvvm.model.UserLocation
import com.example.findmydost.mvvm.viewmodel.MapFragmentViewModel
import com.example.findmydost.services.LocationUpdateSevice
import com.example.findmydost.util.Constants
import com.example.findmydost.util.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.findmydost.util.LocationUtility
import com.github.clans.fab.FloatingActionButton
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MapFragment : Fragment(), EasyPermissions.PermissionCallbacks,GoogleMap.OnMarkerClickListener,
    OnMapReadyCallback {


    private  val TAG = "MapFragment"

    lateinit var binding: FragmentMapBinding


    val INITIAL_ZOOM = 12f
    var mMap: GoogleMap? = null


    private val isLocationPermitted = MutableLiveData<Boolean>();

    private lateinit var lastLocation: Location
    private lateinit var fab_open: Animation
    private  lateinit var fab_close:Animation
    private  lateinit var fab_clock:Animation
    private  lateinit var fab_anticlock:Animation

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    private val viewModel: MapFragmentViewModel by viewModels()

    private  var isopen: Boolean = false;




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
      binding =   DataBindingUtil.inflate<FragmentMapBinding>(layoutInflater,R.layout.fragment_map,null,false)


        fusedLocationProviderClient = FusedLocationProviderClient(requireContext())
//        val mapFragment =
//            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment;
        val mapFragment = SupportMapFragment.newInstance()
        activity?.supportFragmentManager?.beginTransaction()?.add(R.id.fragment_container, mapFragment)?.commit()
        mapFragment.getMapAsync(this)

        fab_open = android.view.animation.AnimationUtils.loadAnimation(requireContext(),R.anim.fab_open);
        fab_close = android.view.animation.AnimationUtils.loadAnimation(requireContext(),R.anim.fab_close);
        fab_clock = android.view.animation.AnimationUtils.loadAnimation(requireContext(),R.anim.fab_rotate_clock);
        fab_anticlock = android.view.animation.AnimationUtils.loadAnimation(requireContext(),R.anim.fab_rotate_anticlock);

        binding.addButton.setOnClickListener {

            if(isopen){
                binding.addButton.startAnimation(fab_clock);
                binding.editButton.startAnimation(fab_open);
                isopen = true;
            }else{
                binding.addButton.startAnimation(fab_anticlock);
                binding.editButton.startAnimation(fab_close);
            }
        }




//        mapFragment.getMapAsync {
//            map = it;
//            if(!it.isMyLocationEnabled){
//                if (ActivityCompat.checkSelfPermission(
//                        requireContext(),
//                        Manifest.permission.ACCESS_FINE_LOCATION
//                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                        requireContext(),
//                        Manifest.permission.ACCESS_COARSE_LOCATION
//                    ) != PackageManager.PERMISSION_GRANTED
//                ) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    return@getMapAsync
//                }
//                it.isMyLocationEnabled = true;
//                val bay = LatLng(37.68, -122.42)
//                it.moveCamera(CameraUpdateFactory.zoomTo(10f))
//                it.moveCamera(CameraUpdateFactory.newLatLng(bay))
//                it.uiSettings.isZoomControlsEnabled = true
//                it.uiSettings.isTiltGesturesEnabled = false
//
//            }
//        }
        requestPermissions()

        isLocationPermitted.observe(viewLifecycleOwner, Observer {

            if(it){
                sendCommandToService(ACTION_START_OR_RESUME_SERVICE);
            }else{

            }
        })

        LocationUpdateSevice.isLocationUpdating.observe(viewLifecycleOwner, Observer {

            updateLocationtoFiebase(it)


        })
        return binding.root;
    }



    private fun sendCommandToService(action: String) {
        Intent(requireContext(), LocationUpdateSevice::class.java).also {
            it.action = action
            requireContext().startService(it)

        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val inflater: MenuInflater? = activity?.menuInflater
        inflater?.inflate(R.menu.map_options, menu)

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {

            R.id.terrain_map -> {
                //    map.setMapType(GoogleMap.MAP_TYPE_TERRAIN)
               mMap?.mapType = GoogleMap.MAP_TYPE_TERRAIN;


                true
            }
            R.id.satellite_map -> {
                //    map.setMapType(GoogleMap.MAP_TYPE_TERRAIN)
                mMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE;


                true
            }
            R.id.hybrid_map -> {
                //    map.setMapType(GoogleMap.MAP_TYPE_TERRAIN)
                mMap?.mapType = GoogleMap.MAP_TYPE_HYBRID;


                true
            }
            R.id.normal_map -> {
                //    map.setMapType(GoogleMap.MAP_TYPE_TERRAIN)
                mMap?.mapType = GoogleMap.MAP_TYPE_NORMAL;


                true
            }

            else -> super.onOptionsItemSelected(item)
        }


    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap;

        mMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE;
        // Pan the camera to your home address (in this case, Google HQ).
        // Pan the camera to your home address (in this case, Google HQ).
        val home =
            LatLng(37.421982, -122.085109)
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(home, INITIAL_ZOOM))

        // Add a ground overlay 100 meters in width to the home location.

        // Add a ground overlay 100 meters in width to the home location.
        val homeOverlay: GroundOverlayOptions = GroundOverlayOptions()
            //.image(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_background))
            .image(bitmapDescriptorFromVector(requireContext(),R.drawable.ic_launcher_background))
            .position(home, 100F)
        mMap!!.addGroundOverlay(homeOverlay);
        setMapLongClick(mMap!!); // Set a long click listener for the map;
        setPoiClick(mMap!!); // Set a click listener for points of interest.
        setMapStyle(mMap!!); // Set the custom map style.
        enableMyLocation(mMap!!); // Enable location tracking.
        // Enable going into StreetView by clicking on an InfoWindow from a
        // point of interest.
        setInfoWindowClickToPanorama(mMap!!);
     //   setUpMap();
    }
    private fun setInfoWindowClickToPanorama(map: GoogleMap) {
        map.setOnInfoWindowClickListener { marker ->
            // Check the tag
            if (marker.tag === "poi") {

                // Set the position to the position of the marker
                val options: StreetViewPanoramaOptions =
                    StreetViewPanoramaOptions().position(
                        marker.position
                    )
                val streetViewFragment: SupportStreetViewPanoramaFragment =
                    SupportStreetViewPanoramaFragment
                        .newInstance(options)

                // Replace the fragment and add it to the backstack
                activity?.supportFragmentManager?.beginTransaction()?.replace(
                        R.id.fragment_container,
                        streetViewFragment
                    )
                     ?.addToBackStack(null)?.commit()
            }
        }
    }

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            val poiMarker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            poiMarker.showInfoWindow()
            poiMarker.tag = getString(R.string.poi)
        }
    }
    private fun enableMyLocation(map: GoogleMap) {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            === PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            map.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION_PERMISSION
            )
        }
    }
    private fun setMapStyle(map: GoogleMap) {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireActivity(), R.raw.map_styles
                )
            )
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }
    private fun setMapLongClick(map: GoogleMap) {

        // Add a blue marker to the map when the user performs a long click.
        map.setOnMapLongClickListener { latLng ->
            val snippet: String = java.lang.String.format(
                Locale.getDefault(),
                getString(R.string.lat_long_snippet),
                latLng.latitude,
                latLng.longitude
            )
            map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.dropped_pin))
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )
        }
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        TODO("Not yet implemented")
    }


  private fun updateLocationtoFiebase(it: Boolean?) {

      if (it!!) {
          if (LocationUtility.hasLocationPermissions(requireContext())) {
              val request = LocationRequest().apply {
                  interval = Constants.LOCATION_UPDATE_INTERVAL
                  fastestInterval = Constants.FASTEST_LOCATION_INTERVAL
                  priority = LocationRequest.PRIORITY_HIGH_ACCURACY
              }
              if (ActivityCompat.checkSelfPermission(
                      requireContext(),
                      Manifest.permission.ACCESS_FINE_LOCATION
                  ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                      requireContext(),
                      Manifest.permission.ACCESS_COARSE_LOCATION
                  ) != PackageManager.PERMISSION_GRANTED
              ) {
                  // TODO: Consider calling
                  //    ActivityCompat#requestPermissions
                  // here to request the missing permissions, and then overriding
                  //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                  //                                          int[] grantResults)
                  // to handle the case where the user grants the permission. See the documentation
                  // for ActivityCompat#requestPermissions for more details.
                  return
              }
              fusedLocationProviderClient.requestLocationUpdates(
                  request,
                  locationCallback,
                  Looper.getMainLooper()
              )
          }
      } else {
          fusedLocationProviderClient.removeLocationUpdates(locationCallback)
      }
  }

    val locationCallback = object : LocationCallback() {
        @SuppressLint("TimberArgCount")
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            if ( LocationUpdateSevice.isLocationUpdating.value!!) {
                result?.locations?.let { locations ->
                    for (location in locations) {


                        var userLocation:UserLocation = UserLocation();
                        userLocation.location = location;
                        viewModel.locationUpdate(userLocation)

                        Timber.d("NEW LOCATION: ${location.latitude}, ${location.longitude}")
                    }
                }
            }

            viewModel.isLocationUpdated.observe(viewLifecycleOwner,Observer{
                if(it){
                    Timber.d(TAG,"location updating");
                }else{
                    Timber.d(TAG,"location not updating");
                }
            })
        }


    }
    private suspend fun sendUserLocation(location: Location){



    }

    private suspend fun delayMethod(){

        delay(60 * 1000);

    }
    private fun requestPermissions(){
        if(LocationUtility.hasLocationPermissions(requireContext())) {
            isLocationPermitted.value = true;
            return
        }
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this app.",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this app.",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }


    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
            isLocationPermitted.value = false;
        } else {
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

        isLocationPermitted.value = true;
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    companion object {

        const val REQUEST_CODE_LOCATION_PERMISSION:Int = 101;
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MapFragment().apply {
                arguments = Bundle().apply {
                   /* putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)*/
                }
            }
    }
    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        val vectorDrawable=ContextCompat.getDrawable(context,vectorResId)
        vectorDrawable!!.setBounds(0,0,vectorDrawable!!.intrinsicWidth,vectorDrawable.intrinsicHeight)
        val bitmap=Bitmap.createBitmap(vectorDrawable.intrinsicWidth,vectorDrawable.intrinsicHeight,Bitmap.Config.ARGB_8888)
        val canvas=Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

}