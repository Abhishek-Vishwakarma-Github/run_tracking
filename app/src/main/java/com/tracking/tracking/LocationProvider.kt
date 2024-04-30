package com.tracking.tracking

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.tracking.tracking.utils.Constant

class LocationProvider(var fusedLocationClient: FusedLocationProviderClient) {
    private var distance: Double = 0.0
    private val path = ArrayList<LatLng>()
    val liveData :LiveData<ArrayList<LatLng>>
    get() = mutableLiveData
    private var mutableLiveData = MutableLiveData<ArrayList<LatLng>>()
    val lastLocationLiveData :LiveData<Location?>
    get() = mutableLastLiveData
    private var mutableLastLiveData = MutableLiveData<Location?>()
    var latLngList = ArrayList<LatLng>()
//    var array1 = ArrayList<String>()
    private var lastLocation : Location? = null
    val distanceLiveData : LiveData<Double>
    get() = mutableDistanceLiveData
    private var mutableDistanceLiveData = MutableLiveData<Double>()

    @SuppressLint("MissingPermission")
    fun getLastLocation()
    {

        Constant.isLastLocationFunCalled = true
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                // Got last known location. In some rare situations this can be null.
                mutableLastLiveData.postValue(location)
            }
    }

    var locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            p0 ?: return
            for (location in p0.locations){
                // Update UI with location data
                // ...

                if(lastLocation != null) {
                    var lastLocat = LatLng(lastLocation!!.latitude,lastLocation!!.longitude)
                    var latLng = LatLng(p0.locations.last().latitude, p0.locations.last().longitude)
                    path.add(latLng)
                    if(path.size >1) {
                        distance +=
                            SphericalUtil.computeDistanceBetween(
                                path.get(path.lastIndex - 1),
                                path.last()
                            )
                        mutableDistanceLiveData.postValue(distance)
                    }
                    else{
                        distance +=
                            SphericalUtil.computeDistanceBetween(
                                lastLocat,
                                path.last()
                            )
                        mutableDistanceLiveData.postValue(distance)
//                        binding.runDistance.text =
//                            "%2f KM".format(distance / 1000.0).toString()

                    }
//                    path.add(latLng)

//            polyline?.remove()
                }
                else{
                    lastLocation = p0.lastLocation!!
                }
                var latLng = LatLng(p0.locations.last().latitude,p0.locations.last().longitude)
                latLngList.add(latLng)
            }
            mutableLiveData.postValue(latLngList)

        }
    }
    var locationRequest = LocationRequest.Builder(7000).setIntervalMillis(6000)
//        .setFastestIntervalMillis(5000)
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        .build()

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }
    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
    fun reset()
    {
        path.clear()
        distance = 0.0;
        latLngList.clear()
        lastLocation = null
//        mutableLastLiveData.postValue(null);
    }
}