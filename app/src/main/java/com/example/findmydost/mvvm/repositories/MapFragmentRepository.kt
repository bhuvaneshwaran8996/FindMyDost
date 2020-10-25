package com.example.findmydost.mvvm.repositories

import com.example.findmydost.mvvm.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject


class MapFragmentRepository @Inject constructor(private val mFiebaseFirestoe:FirebaseFirestore, private val mFiebaseAuth:FirebaseAuth){

    @Inject private lateinit var mUser: User;

    public suspend fun updateOnlineStatus(online:Boolean): Task<Void> {

        mUser.online = online;

      return  mFiebaseFirestoe.collection("users").document(mFiebaseAuth.uid.toString())

            .set(mUser)

    }



}