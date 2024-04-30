package com.tracking.tracking.viewmodel

import android.location.Location
import androidx.lifecycle.LiveData
import com.google.android.gms.maps.model.LatLng
import com.tracking.tracking.repository.UserRepository

class MapViewModel(private val userRepository: UserRepository) {

    val liveData : LiveData<ArrayList<LatLng>>
    get() = userRepository.liveData

    val lastLocLivData : LiveData<Location?>
        get() = userRepository.lastLocLivData

    val distanceLiveData : LiveData<Double>
        get() = userRepository.distanceLiveData
    fun startTracking()
    {
        userRepository.startTracking()
    }

    fun getLastLocation()
    {
        userRepository.getLastLocation()
    }

    fun stopTracking() {
        userRepository.stopTracking()
    }
    fun reset()
    {
        userRepository.reset()
    }
}