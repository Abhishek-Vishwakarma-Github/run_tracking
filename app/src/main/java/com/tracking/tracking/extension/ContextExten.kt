package com.tracking.tracking

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

fun Context.hasLocationPermission():Boolean
{
    return if(ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {

        return false;
    }
    else
    {
        return true
    }

}
fun Context.openAppSystemSettings() {
    startActivity(Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        data = Uri.fromParts("package", packageName, null)
    })
}