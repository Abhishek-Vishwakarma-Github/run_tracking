package com.tracking.tracking.ui

import android.annotation.SuppressLint
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.tracking.tracking.*
import com.tracking.tracking.application.AppApplication
import com.tracking.tracking.databinding.ActivityMapsBinding
import com.tracking.tracking.repository.UserRepository
import com.tracking.tracking.utils.Constant
import com.tracking.tracking.viewmodel.MapViewModel

//import javax.security.auth.login.LoginException

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val path = ArrayList<LatLng>()
    private var mTimeWhenPaused: Long = 0
    private  var lastLocation: LatLng? = null
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationProvider: LocationProvider
    private lateinit var mapViewModel: MapViewModel
    private lateinit var userRepository: UserRepository
    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
                mapViewModel.getLastLocation()

            }
            permissions.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                mapViewModel.getLastLocation()

            }
            else -> {
                // No location access granted.
                Constant.locationPermissionCount++
                if(Constant.locationPermissionCount >= 3)
                {
                    this.openAppSystemSettings()
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide();

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Apply the insets as a margin to the view. This solution sets
            // only the bottom, left, and right dimensions, but you can apply whichever
            // insets are appropriate to your layout. You can also update the view padding
            // if that's more appropriate.
            v.updatePadding(bottom = insets.bottom)


            // Return CONSUMED if you don't want want the window insets to keep passing
            // down to descendant views.
            WindowInsetsCompat.CONSUMED
        }
        locationProvider = (application as AppApplication).appContainer.getLocationProvider(this);
        userRepository = (application as AppApplication).appContainer.getUserRepository(this,locationProvider);
        mapViewModel = MapViewModel(userRepository);

        if (this.hasLocationPermission()) {
            if(isNetworkAvailable()) {
                if (isLocationEnable()) {
                    Constant.isLastLocationFunCalled = true
                    mapViewModel.getLastLocation()
                } else {
                    showToast("Turn on Location")
                }
            }else
            {
                showToast("No Internet Connection")

            }
        }
        else {
//            permissionManager.requestPermission();
            requestPermission()
        }

        mapViewModel.distanceLiveData.observe(this, Observer {
            binding.runDistance.text = "%.2f KM".format(it / 1000.0)


        })
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        binding.runFinish.setOnClickListener {

            if(this.hasLocationPermission()&& isLocationEnable())
            {
                reset();
                path.clear();
                mMap.clear()
                binding.runDistance.text = "0.00 KM"
                binding.runResume.text = getString(R.string.start_tracking)
                mapViewModel.reset()
                mapViewModel.stopTracking()
            }
        }
        binding.runResume.setOnClickListener {
            if(this.hasLocationPermission())
            {
                if(isLocationEnable()) {
                    if(isNetworkAvailable()) {
                        if (binding.runResume.text == getString(R.string.start_tracking)) {
                            binding.chronometerTimer.base = SystemClock.elapsedRealtime()
                            binding.chronometerTimer.start()

                            if (Constant.locationPermissionCount >= 3 || Constant.isRestart || (Constant.isLastLocationFunCalled != true)) {
                                mapViewModel.getLastLocation()
//                            Constant.isLastLocationFunCalled = true;
                            }
                            mapViewModel.startTracking()
                            binding.runResume.text = getString(R.string.pause_tracking)
                        } else if (binding.runResume.text == getString(R.string.pause_tracking)) {

                            binding.runResume.text = getString(R.string.Resume_tracking)
                            pause()
                            mapViewModel.stopTracking()
                        } else {
                            binding.runResume.text = getString(R.string.pause_tracking)
                            resume()
                            mapViewModel.startTracking()

                        }
                    }
                    else{
                        showToast("No Internet Connection")
                    }
                }
                else
                {
                    showToast("Turn on Location")
                }
            }
            else{

                requestPermission()
            }
        }
    }

    private fun showToast(str:String) {
        Toast.makeText(this,str, Toast.LENGTH_SHORT).show()
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
//
//        mMap.setMyLocationEnabled(true);
        mMap.clear()
        path.clear()
        mapViewModel.lastLocLivData.observe(this, Observer {

            if(it != null) {
                mMap.setMyLocationEnabled(true);

//                lastLocation = it;
                val lastLocation = LatLng(it!!.latitude, it!!.longitude)

                mMap.addMarker(MarkerOptions().position(lastLocation).title("Marker in Sydney"))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 18f))
            }
            else
            {
                mapViewModel.getLastLocation()
            }
            })

        mapViewModel.liveData.observe(this, Observer {



                mMap.setMyLocationEnabled(true);

                if(lastLocation != null) {
                    var latLng = LatLng(it.last().latitude, it.last().longitude)
                    path.clear()
                    path.addAll(it)

//                    path.add(latLng)
                    val polylineOptions = PolylineOptions()
                        .add(LatLng(lastLocation!!.latitude, lastLocation!!.longitude))
                        .addAll(path)
                        .color(android.graphics.Color.BLACK)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))

//            polyline?.remove()
                    googleMap.addPolyline(polylineOptions)
                }
            else
                {
                    lastLocation = it.first()
                }
        })
        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
    fun resume() {
        binding.chronometerTimer.setBase(SystemClock.elapsedRealtime() - mTimeWhenPaused)
        binding.chronometerTimer.start()
    }

    /**
     * Pause the timer.
     */
    fun pause() {
        binding.chronometerTimer.stop()
        mTimeWhenPaused = SystemClock.elapsedRealtime() - binding.chronometerTimer.getBase()
    }
    fun requestPermission() {
        locationPermissionRequest.launch(
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
    fun reset() {
        Constant.isRestart = true;
        binding.chronometerTimer.stop()
        binding.chronometerTimer.setBase(SystemClock.elapsedRealtime())
        mTimeWhenPaused = 0
        lastLocation = null


    }

    fun isLocationEnable():Boolean
    {
        var locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        var isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        return isGpsEnabled
    }
    override fun onDestroy() {
        super.onDestroy()
        mapViewModel.stopTracking();
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}