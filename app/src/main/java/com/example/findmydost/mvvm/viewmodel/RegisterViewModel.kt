package com.example.findmydost.mvvm.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.findmydost.mvvm.repositories.RegisterRepository

public class RegisterViewModel  @ViewModelInject constructor(val registerRepository: RegisterRepository):ViewModel(){


    public fun registerUser(){

        registerRepository.registerUser()
    }






}