package com.example.findmydost.mvvm.model

import android.net.Uri
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class User @Inject constructor(){

     var displayName:String? = null;
     var email:String? = null;
     var photoUrl: Uri? = null;
     var authType:String? = null;
     var phoneNumber:String? = null;
     






}