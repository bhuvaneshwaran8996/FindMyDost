package com.example.findmydost.mvvm.repositories

import android.location.Location
import com.example.findmydost.mvvm.model.User
import com.example.findmydost.mvvm.model.UserLocation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

public class MapFragmentRepository @Inject constructor(private val mFiebaseFirestoe:FirebaseFirestore){

    @Inject public lateinit var mUser: User;


    public suspend fun updateOnlineStatus(online:Boolean): Task<Void> {

        mUser.online = online;

      return  mFiebaseFirestoe.collection("users").document(FirebaseAuth.getInstance().uid.toString())
          .set(mUser)

    }

    public fun locationUpdate(location:UserLocation):Task<Void> {

        return   mFiebaseFirestoe.collection("user_locations").document(FirebaseAuth.getInstance().uid.toString())
            .set(location)

    }





}