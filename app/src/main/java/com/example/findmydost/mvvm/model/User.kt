package com.example.findmydost.mvvm.model

import android.net.Uri
import com.google.firebase.firestore.ServerTimestamp

import java.io.Serializable
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
 class User  @Inject constructor():Serializable{

     var displayName:String? = null
     var email:String? = null
     var photoUrl: String? = null
     var authType:String? = null
     var phoneNumber:String? = null
     var uid:String? = null
     @ServerTimestamp
     var  serverTimeStamo: Date?=null
}







