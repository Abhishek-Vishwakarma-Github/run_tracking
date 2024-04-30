package com.tracking.tracking.application

import android.app.Application
import com.tracking.tracking.container.AppContainer

class AppApplication: Application() {

    lateinit var appContainer: AppContainer
    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer();
    }

}