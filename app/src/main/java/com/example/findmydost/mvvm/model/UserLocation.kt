package com.example.findmydost.mvvm.model

import android.location.Location
import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*


 class UserLocation : Serializable {


     public var location:Location?=null;
    @ServerTimestamp
    public  var  serverTimeStamo: Date?=null
}