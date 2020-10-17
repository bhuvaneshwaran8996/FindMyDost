package com.example.findmydost.mvvm.viewmodel

import androidx.lifecycle.ViewModel
import com.example.findmydost.mvvm.view.activities.SplashActivity

public class SplashViewModel constructor(): ViewModel(){

    public lateinit var splashNavigator:SplashActivity;



    public fun setNavigator(splashNavigator:SplashActivity){
        this.splashNavigator = splashNavigator;
    }





}