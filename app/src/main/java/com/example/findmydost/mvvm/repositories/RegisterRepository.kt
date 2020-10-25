package com.example.findmydost.mvvm.repositories

import android.util.Log
import android.widget.Toast
import com.example.findmydost.mvvm.model.User
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

public interface IRepository {

    fun handleFacebookAccessToken(accessToken: AccessToken?): Task<AuthResult>
    fun firebaseAuthWithGoogle(idToken: String): Task<AuthResult>
    fun  registerUser():Task<Void>
}

public class RegisterRepository @Inject constructor(
    var mfiebaseFiestore: FirebaseFirestore,
    var mFirebaseAuth: FirebaseAuth
) : IRepository {

    @Inject
    lateinit var mGoogleSingnInClient: GoogleSignInClient;

    @Inject
    lateinit var mUser: User;

    private val TAG = "RegisterRepository"
    override fun handleFacebookAccessToken(accessToken: AccessToken?): Task<AuthResult> {
        Log.d(TAG, "handleFacebookAccessToken: " + accessToken)
        val credential = FacebookAuthProvider.getCredential(accessToken?.token!!)
        return mFirebaseAuth.signInWithCredential(credential)

    }

    override fun firebaseAuthWithGoogle(idToken: String): Task<AuthResult> {


        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return mFirebaseAuth.signInWithCredential(credential)

    }

    override  fun registerUser():Task<Void> {


        return mfiebaseFiestore.collection("users").document(mFirebaseAuth.uid.toString())
            .set(mUser)


    }


}