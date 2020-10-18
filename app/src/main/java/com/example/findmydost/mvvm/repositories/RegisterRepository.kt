package com.example.findmydost.mvvm.repositories

import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton

interface IRepository{

    suspend fun registerUser();
}


public class RegisterRepository @Inject constructor(var mFireStoreInstance: FirebaseFirestore):IRepository{


    override suspend fun registerUser() {



    }
}