package com.tracking.tracking.repository

import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import com.google.android.gms.maps.model.LatLng
import com.tracking.tracking.LocationProvider
import com.tracking.tracking.LocationService

class UserRepository(var activity: AppCompatActivity,var locationProvider: LocationProvider) {
    //    var permissionManager = PermissionManager(activity)
//    private lateinit
    val liveData: LiveData<ArrayList<LatLng>>
        get() = locationProvider.liveData
    val lastLocLivData: LiveData<Location?>
        get() = locationProvider.lastLocationLiveData

    val distanceLiveData : LiveData<Double>
    get() = locationProvider.distanceLiveData
    fun startTracking() {
        var intent = Intent(activity, LocationService::class.java)
        intent.action = LocationService.START_ACTION
        activity.startService(intent)
    }

    fun getLastLocation()
    {
        locationProvider.getLastLocation()
    }

    fun stopTracking() {
//        locationProvider.stopLocationUpdates()
        var intent = Intent(activity, LocationService::class.java)
        intent.action = LocationService.STOP_ACTION
        activity.startService(intent)
    }
    fun reset()
    {
        locationProvider.reset()
    }
}