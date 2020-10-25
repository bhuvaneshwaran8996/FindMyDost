package com.example.findmydost.mvvm.view.activities

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_MULTIPLE_TASK
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.findmydost.DostApplication
import com.example.findmydost.R
import com.example.findmydost.databinding.ActivityRegisterBinding
import com.example.findmydost.local.prefs.PreferenceHelperImp
import com.example.findmydost.mvvm.model.User
import com.example.findmydost.mvvm.viewmodel.RegisterViewModel
import com.example.findmydost.util.LoginState
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
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_register.*
import javax.inject.Inject

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = "RegisterActivity"
    private lateinit var binding: ActivityRegisterBinding;
    private lateinit var mFirebaseUser: FirebaseUser;
    private var mAuthListener: AuthStateListener? = null

    private val registerViewModel: RegisterViewModel by viewModels()

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

    var firebaseUser: FirebaseUser? = null;

    @Inject
    lateinit var mFireStoreInstance: FirebaseFirestore;

    var loginType: String? = null;


    override fun onStart() {
        super.onStart()

        if (mFirebaseAuth.currentUser != null) {
            if (mPreferenceHelperImp.getLoginData().equals("1")) {

                loginType = "FB";

            } else if (mPreferenceHelperImp.getLoginData().equals("2")) {

                loginType = "GOOGLE";

            }
            createUserObject(mFirebaseAuth.currentUser)

            navigateToDostActivity();
        }
    }

    public fun navigateToDostActivity() {


        val intent = Intent(this, DostActivity::class.java);
        intent.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent);
        finish();
    }

    override fun onStop() {
        super.onStop()

    }
    fun createUserObject(firebaseUser: FirebaseUser?) {
        mUser.email = firebaseUser?.email;
        mUser.photoUrl = firebaseUser?.photoUrl.toString();
        mUser.phoneNumber = firebaseUser?.phoneNumber;
        mUser.email = firebaseUser?.email;
        mUser.displayName = firebaseUser?.displayName;
        mUser.authType = loginType;
        mUser.uid = firebaseUser?.uid;


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        binding.registerFacebook.setOnClickListener(this)
        binding.registerGoogle.setOnClickListener(this);

        var measuredHeight = binding.registerFacebook.measuredHeight




        registerViewModel.loginMediatorLiveData.observe(this, Observer {

            when (it.loginStatus) {

                LoginState.LOGGED_IN_FB -> pocessFbAuth()
                LoginState.LOGGED_IN_GOOGLE -> pocessGoogleAuth()
                LoginState.LOADING -> binding.myProgressBar.visibility = View.VISIBLE;
                LoginState.LOGIN_FAILED -> failureAuth(it.msg)

            }


        })

        registerViewModel.userRegisteLiveData.observe(this, Observer{

            if(it){
                navigateToDostActivity()
            }else{
                Toast.makeText(this, "something went wrong, please ty again", Toast.LENGTH_LONG).show()
            }

        })



    }

    fun failureAuth(msg: String) {

        binding.myProgressBar.visibility = View.GONE;
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    fun pocessGoogleAuth() {
        mPreferenceHelperImp.saveLoginData(2);
        loginType = "GOOGLE";
        createUserObject(mFirebaseAuth?.currentUser)
        binding.myProgressBar.visibility = View.GONE;
        registerViewModel.registerUser();



    }

    fun pocessFbAuth() {


        mPreferenceHelperImp.saveLoginData(1);
        loginType = "FB";
        createUserObject(mFirebaseAuth?.currentUser)
        binding.myProgressBar.visibility = View.GONE;
        registerViewModel.registerUser();

    }

    override fun onClick(view: View?) {

        when (view?.id) {
            R.id.register_facebook -> setupFacebookAuth()
            R.id.register_google -> setupGoogleAuth()
        }
    }

    fun setupGoogleAuth() {

//        mAuthListener?.let { mFirebaseAuth.removeAuthStateListener(it) };
        val signInIntent = mGoogleSingnInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun setupFacebookAuth() {

        binding.registerFacebook.setReadPermissions("email", "public_profile")
        binding.registerFacebook.registerCallback(mCallbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                registerViewModel.handleFacebookAccessToken(loginResult.accessToken);


            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
                // ...
            }

            override fun onError(error: FacebookException) {
                LoginManager.getInstance().logOut()
                Toast.makeText(this@RegisterActivity,error.toString(),Toast.LENGTH_LONG).show();
                Log.d(TAG, "facebook:onError", error)
                // ...
            }
        })


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
                registerViewModel.firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // ...
            }
        }
    }


    companion object {

        private val RC_SIGN_IN = 101;
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()

    }
}

