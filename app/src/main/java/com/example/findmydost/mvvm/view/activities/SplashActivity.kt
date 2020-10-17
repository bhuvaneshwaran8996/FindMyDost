package com.example.findmydost.mvvm.view.activities

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.example.findmydost.R
import com.example.findmydost.interfaces.ISplashActivity

class SplashActivity: AppCompatActivity(),ISplashActivity{


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_splash)
    }

    override fun navigateSplash() {


    }
}