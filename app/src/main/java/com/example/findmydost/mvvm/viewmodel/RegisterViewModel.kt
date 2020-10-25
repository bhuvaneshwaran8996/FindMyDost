package com.example.findmydost.mvvm.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.findmydost.mvvm.repositories.RegisterRepository
import com.example.findmydost.util.LoginState
import com.example.findmydost.util.LoginStatus
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import javax.inject.Inject

class RegisterViewModel @ViewModelInject constructor(val registerRepository: RegisterRepository) :
    ViewModel() {

    @Inject
    lateinit var mfiebaseFiestore: FirebaseFirestore

    @Inject
    lateinit var mUser: FirebaseUser

    @Inject
    lateinit var mFirebaseAuth: FirebaseAuth

    @Inject
    lateinit var loginManager: LoginManager

    @Inject
    lateinit var mGoogleSingnInClient: GoogleSignInClient


    val loginMediatorLiveData = MediatorLiveData<LoginStatus>()
    val loginLiveData = MutableLiveData<LoginStatus>()
    val userRegisteLiveData = MutableLiveData<Boolean>()


    fun handleFacebookAccessToken(accessToken: AccessToken?) {


        obseveLoading()
        viewModelScope.launch {
            registerRepository.handleFacebookAccessToken(accessToken)
                .addOnCompleteListener {
                    if (it.isSuccessful) {

                        var loginStatus =
                            LoginStatus("", LoginState.LOGGED_IN_FB)
                        loginLiveData.value = loginStatus
                        obseveLiveData(loginLiveData)

                    } else {

                        LoginManager.getInstance().logOut();
                        var loginStatus =
                            LoginStatus(it.exception.toString(), LoginState.LOGIN_FAILED)
                        loginLiveData.value = loginStatus
                        obseveLiveData(loginLiveData)

                    }
                }
        }

    }

    private fun obseveLiveData(loginLiveData: MutableLiveData<LoginStatus>) {
        loginMediatorLiveData.addSource(loginLiveData, Observer {
            loginMediatorLiveData.value = it
            loginMediatorLiveData.removeSource(loginLiveData)


        })

    }

    fun firebaseAuthWithGoogle(idToken: String) {
        obseveLoading()
        viewModelScope.launch {
            registerRepository.firebaseAuthWithGoogle(idToken).addOnCompleteListener {

                if (it.isSuccessful) {


                    var loginStatus = LoginStatus("", LoginState.LOGGED_IN_GOOGLE)
                    loginLiveData.value = loginStatus
                    obseveLiveData(loginLiveData)


                } else {

                    var loginStatus = LoginStatus(it.exception.toString(), LoginState.LOGIN_FAILED)
                    loginLiveData.value = loginStatus
                    obseveLiveData(loginLiveData)

                }
            }
        }
    }

    fun obseveLoading() {
        var loginStatus = LoginStatus("", LoginState.LOADING)
        loginLiveData.value = loginStatus
        obseveLiveData(loginLiveData)
    }

    fun signOut() {

        // Firebase sign out
        mFirebaseAuth.signOut()
        loginManager.logOut()
        // Google sign out
        mGoogleSingnInClient.signOut().let {
            if (it.isSuccessful) {

                // updateUI(null)
            }
        }
        var loginStatus = LoginStatus("", LoginState.LOGGED_OUT)
        loginLiveData.value = loginStatus
        obseveLiveData(loginLiveData)

    }

    fun registerUser():Unit{
        registerRepository.registerUser().addOnCompleteListener{
            userRegisteLiveData.value = it.isSuccessful
        }

    }

}