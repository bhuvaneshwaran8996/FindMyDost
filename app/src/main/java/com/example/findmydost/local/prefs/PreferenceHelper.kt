package com.example.findmydost.local.prefs

import android.content.SharedPreferences
import javax.inject.Inject


interface PreferenceHelperImp{
    fun saveLoginData(mode: Int)
    fun getLoginData():String?
}
class PreferenceHelper  @Inject constructor(private val sharedPreferences: SharedPreferences):PreferenceHelperImp {


    override fun saveLoginData(mode: Int) {

        sharedPreferences.edit().putString("logintype",mode.toString()) //0 means logout, 1 means fb, 2 means google
            .apply()
    }

    override fun getLoginData(): String? {

       return sharedPreferences.getString("logintype",0.toString())
    }
}