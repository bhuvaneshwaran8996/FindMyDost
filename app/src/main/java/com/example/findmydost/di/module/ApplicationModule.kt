package com.example.findmydost.di.module

import android.content.Context
import android.content.SharedPreferences
import com.example.findmydost.local.prefs.PreferenceHelper
import com.example.findmydost.local.prefs.PreferenceHelperImp
import com.example.findmydost.util.Constants
import com.facebook.login.LoginManager
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
abstract class ApplicationModule {


    @Binds
    @Singleton
    abstract fun bindPreferenceData(preferenceHelper: PreferenceHelper):PreferenceHelperImp

    companion object{
        @Provides
        @Singleton
        fun  provideSharedPreferences(@ApplicationContext context: Context):SharedPreferences  = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);


        @Provides
        @Singleton
        fun provideLoginManager() = LoginManager.getInstance();

        @Provides
        @Singleton
        fun provideBaseUrl() = Constants.BASE_URL


        @Provides
        @Singleton
        fun provideFireStoreInstance():FirebaseFirestore = FirebaseFirestore.getInstance()









    }





}