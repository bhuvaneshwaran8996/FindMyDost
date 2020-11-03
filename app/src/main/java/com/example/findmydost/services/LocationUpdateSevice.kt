package com.example.findmydost.services


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.findmydost.util.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.findmydost.util.Constants.FASTEST_LOCATION_INTERVAL
import com.example.findmydost.util.Constants.LOCATION_UPDATE_INTERVAL
import com.example.findmydost.util.Constants.NOTIFICATION_CHANNEL_ID
import com.example.findmydost.util.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.findmydost.util.Constants.NOTIFICATION_ID
import com.example.findmydost.util.LocationUtility
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LocationUpdateSevice : LifecycleService() {



    @Inject
    lateinit var baseNotificationBuilder : NotificationCompat.Builder

    // var curNotificationBuilder: NotificationCompat.Builder?=null;


    private var isupdating: Boolean  = false;



    companion object {

        val isLocationUpdating = MutableLiveData<Boolean>();
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (!isupdating) {
                        startForgoundLocationServices()
                        isLocationUpdating.value = true;

                    } else {
                        Timber.d("Resuming service...")

                    }
                }

            }
        }
        return super.onStartCommand(intent, flags, startId)


    }

    fun startForgoundLocationServices() {
        isLocationUpdating.postValue(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }
        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())

        isLocationUpdating.observe(this, Observer {


            var notification = baseNotificationBuilder
                .setContentText("Location is tracking")
                notificationManager.notify(NOTIFICATION_ID,notification.build())

        })

    }


    override fun onCreate() {
        postInitialValues();
        isLocationUpdating.observe(this, Observer{
            isupdating = true;


        })
        super.onCreate()
    }

    private fun postInitialValues() {
        isupdating = false;
      //  curNotificationBuilder = baseNotificationBuilder;
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }


}