package com.tracking.tracking.container

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.LocationServices
import com.tracking.tracking.LocationProvider
import com.tracking.tracking.repository.UserRepository

class AppContainer {
    private  var locationProvider : LocationProvider? = null

    fun getLocationProvider(context: Context): LocationProvider {
        if(locationProvider == null)
        {
            locationProvider = LocationProvider(LocationServices.getFusedLocationProviderClient(context))
        }
        return locationProvider!!;
    }
    fun getUserRepository(activity: AppCompatActivity,locationProvider: LocationProvider): UserRepository {
        return UserRepository(activity,locationProvider);
    }
}