package com.tracking.tracking

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.tracking.tracking.application.AppApplication

class LocationService : Service() {

    private lateinit var locationProvider: LocationProvider
//    val liveData : LiveData<Location>
//    get() = locationProvider.liveData
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }


    override fun onCreate() {
        super.onCreate()
        locationProvider = (application as AppApplication).appContainer.getLocationProvider(this);
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action)
        {
            START_ACTION -> start()
            STOP_ACTION -> stop()
        }

        return super.onStartCommand(intent, flags, startId)

    }

    fun stop()
    {
        stopForeground(true)
        stopSelf()
        locationProvider.stopLocationUpdates()
    }

    private fun start() {
         var notification = NotificationCompat.Builder(this,"location")
             .setContentTitle(getString(R.string.app_name))
             .setContentText("Running....")
             .setSmallIcon(R.drawable.running)
//             .setLargeIcon(R.drawable.running)
             .setOngoing(true)

        locationProvider.startLocationUpdates()

        startForeground(1,notification.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        stop()
    }
    companion object{
        val  START_ACTION = "START_ACTION"
        val  STOP_ACTION = "STOP_ACTION"
    }
}