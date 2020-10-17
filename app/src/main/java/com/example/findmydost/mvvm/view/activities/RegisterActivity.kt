package com.example.findmydost.mvvm.view.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.findmydost.R
import com.example.findmydost.databinding.ActivityRegisterBinding
import com.example.findmydost.local.prefs.PreferenceHelperImp
import com.example.findmydost.mvvm.model.User
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_register.*
import javax.inject.Inject

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = "RegisterActivity"
    private lateinit var binding: ActivityRegisterBinding;
    private lateinit var mFirebaseUser: FirebaseUser;
    private var mAuthListener: AuthStateListener? = null

    @Inject
    lateinit var mGoogleSingnInClient: GoogleSignInClient;

    @Inject
    lateinit var mCallbackManager: CallbackManager;

    @Inject
    lateinit var mFirebaseAuth: FirebaseAuth;

    @Inject
    lateinit var mUser: User;


    @Inject
    lateinit var mPreferenceHelperImp: PreferenceHelperImp;

    @Inject
    lateinit var loginManager: LoginManager;

    var loginType: String? = null;


    override fun onStart() {
        super.onStart()

        if (mFirebaseAuth.currentUser != null) {
            mAuthListener?.let {
                mFirebaseAuth.addAuthStateListener(it)
                if (mFirebaseAuth.currentUser != null) {


                    updateUI(mFirebaseAuth.currentUser)


                    //move to main activity

                } else {
                    mAuthListener?.let {
                        mFirebaseAuth.removeAuthStateListener(it)
                    }
                }

            }
        }
    }

    override fun onStop() {
        super.onStop()
        mAuthListener?.let { mFirebaseAuth.removeAuthStateListener(it) };
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.loginButton.setOnClickListener(this)
        binding.signInButton.setOnClickListener(this);
        binding.logout.setOnClickListener {
            signOut()
        };
        mAuthListener = AuthStateListener { firebaseAuth: FirebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                // User is signed in
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.uid)
            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out")
            }
            if (user != null) {



                Log.d(
                    TAG,
                    """
                        User details : ${user.displayName}${user.email}
                        ${user.photoUrl}
                        ${user.uid}
                        ${user.getIdToken(true)}
                        ${user.providerId}
                        """.trimIndent()


                )
            }
        }

}

    override fun onClick(view: View?) {

        when (view?.id) {
            R.id.login_button -> setupFacebookAuth()
            R.id.sign_in_button -> setupGoogleAuth()
        }
    }

    fun setupGoogleAuth() {

        val signInIntent = mGoogleSingnInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

     fun setupFacebookAuth() {
        binding.myProgressBar.visibility = View.VISIBLE;
        binding.loginButton.setReadPermissions("email", "public_profile")
        binding.loginButton.registerCallback(mCallbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
                // ...
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
                // ...
            }
        })


    }

    private fun handleFacebookAccessToken(accessToken: AccessToken?) {
        Log.d(TAG, "handleFacebookAccessToken: " + accessToken)
        val credential = FacebookAuthProvider.getCredential(accessToken?.token!!)
        mFirebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    loginType = "FB";
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = FirebaseAuth.getInstance().currentUser
                    mPreferenceHelperImp.saveLoginData(1) //fb

                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
//                    Toast.makeText(
//                        baseContext, "Authentication failed.",
//                        Toast.LENGTH_SHORT
//                    ).show()
                    updateUI(null)
                }

                // ...
            }



    }

    private fun updateUI(user: FirebaseUser?) {

        try{
            if(user!=null){
                binding.myProgressBar.visibility = View.GONE;


                mUser.displayName = user.displayName
                mUser.email = user.email
                mUser.photoUrl = user.photoUrl
                mUser.phoneNumber = user.phoneNumber;
                mUser.authType = mPreferenceHelperImp.getLoginData();



                startActivity(Intent(this@RegisterActivity, DostActivity::class.java))
                Toast.makeText(
                    baseContext, user?.email,
                    Toast.LENGTH_SHORT
                ).show()




            }else{
                Toast.makeText(
                    baseContext, "user signed out",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }catch (e:Exception){
            Toast.makeText(this@RegisterActivity,"Authentication failed",Toast.LENGTH_SHORT).show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        mCallbackManager.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // ...
            }
        }
    }


    private fun firebaseAuthWithGoogle(idToken: String) {
        binding.myProgressBar.visibility = View.VISIBLE;
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mFirebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    loginType = "GOOGLE";
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = mFirebaseAuth.currentUser
                    mPreferenceHelperImp.saveLoginData(2) //fb
                    updateUI(user)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    // ...
                    Snackbar.make(root_view, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                    updateUI(null)
                }

                // ...
            }
    }
     private fun signOut() {

         // Firebase sign out
         mFirebaseAuth.signOut()
        loginManager.logOut();
         // Google sign out
        mGoogleSingnInClient.signOut().let {
            if(it.isSuccessful){

                updateUI(null)
            }
        }
     }

    companion object {

        private val RC_SIGN_IN = 101;
    }
}

