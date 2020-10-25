package com.example.findmydost.mvvm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findmydost.mvvm.repositories.MapFragmentRepository
import com.example.findmydost.mvvm.repositories.RegisterRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class MapFragmentViewModel:ViewModel() {

    @Inject private lateinit var mapFragmentRepository: MapFragmentRepository
    public val isStatusUpdated = MutableLiveData<Boolean>();


    public fun updateOnlineStatus(online:Boolean):LiveData<Boolean>{
        viewModelScope.launch {
            mapFragmentRepository.updateOnlineStatus(online).addOnCompleteListener {

                isStatusUpdated.value = it.isSuccessful
            }

        }
        return isStatusUpdated;
    }

}