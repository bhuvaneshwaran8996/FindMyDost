package com.example.findmydost.di.module


import android.content.Context
import com.example.findmydost.BuildConfig
import com.example.findmydost.R
import com.example.findmydost.util.Constants
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {

/*    @Provides
    @Singleton
    fun provideBaseUrl() = Constants.BASE_URL*/



    @Provides
    @ActivityScoped
    fun provideGoogleSignInOptions(@ActivityContext context: Context ): GoogleSignInOptions {

        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build();
    }

    @Provides
    @ActivityScoped
    fun provideGoogleSignInClient(@ActivityContext context: Context ,googleSignInOptions: GoogleSignInOptions): GoogleSignInClient {
        return GoogleSignIn.getClient(context.applicationContext, googleSignInOptions)
    }

    @Provides
    @ActivityScoped
    fun provideFbCallBackManager(): CallbackManager{
        return CallbackManager.Factory.create();
    }

    @Provides
    @ActivityScoped
    fun provideFirebaseAuth(): FirebaseAuth {

        return  FirebaseAuth.getInstance();
    }









}