package com.tracking.tracking.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tracking.tracking.databinding.ActivitySplashBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var channel = NotificationChannel(
                "location",
                "Location", NotificationManager.IMPORTANCE_LOW
            )
            val notificationManger =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManger.createNotificationChannel(channel)
        }
        binding.imageView.animate().scaleX(1.3f).scaleY(1.3f).setDuration(2000).start()
        CoroutineScope(Dispatchers.IO).launch {
            delay(2000)
            var intent = Intent(this@SplashActivity, MapsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}