package com.example.findmydost

import android.app.Application
import com.example.findmydost.mvvm.model.User
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree

@HiltAndroidApp
public class DostApplication: Application(){


    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

        FacebookSdk.sdkInitialize(applicationContext);
        AppEventsLogger.activateApp(this);
    }


}