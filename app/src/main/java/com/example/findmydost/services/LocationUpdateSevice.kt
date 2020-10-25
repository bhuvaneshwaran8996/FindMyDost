package com.example.findmydost.services

import android.content.Intent
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocationUpdateSevice: LifecycleService() {


    companion object{

         val isLocationUpdating = MutableLiveData<Boolean>();
    }



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)


    }

    override fun onCreate() {
        postInitialValues();
        super.onCreate()
    }

    private fun postInitialValues() {
        isLocationUpdating.value = false;



    }


}