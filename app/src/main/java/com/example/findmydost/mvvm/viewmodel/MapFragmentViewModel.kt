package com.example.findmydost.mvvm.viewmodel

import android.location.Location
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findmydost.mvvm.model.UserLocation
import com.example.findmydost.mvvm.repositories.MapFragmentRepository
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.launch
import javax.inject.Inject


public class MapFragmentViewModel @ViewModelInject constructor(public  var mapFragmentRepository: MapFragmentRepository):ViewModel() {

/*
    @Inject
    lateinit var mapFragmentRepository: MapFragmentRepository;*/

    val isStatusUpdated = MutableLiveData<Boolean>();
    val isLocationUpdated = MutableLiveData<Boolean>();


    public fun updateOnlineStatus(online: Boolean): LiveData<Boolean> {
        viewModelScope.launch {
            mapFragmentRepository.updateOnlineStatus(online).addOnCompleteListener {

                isStatusUpdated.value = it.isSuccessful
            }

        }
        return isStatusUpdated;
    }

    public fun locationUpdate(location: UserLocation): Unit {


            mapFragmentRepository.locationUpdate(location).addOnCompleteListener {
                if (it.isSuccessful) {
                    isLocationUpdated.postValue(true);


                } else {
                    isLocationUpdated.postValue(false);
                }
            }


    }


}