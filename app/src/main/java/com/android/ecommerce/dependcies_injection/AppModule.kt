package com.android.ecommerce.dependcies_injection

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.android.ecommerce.firebase.FirebaseCommon
import com.android.ecommerce.util.Constants.INTRODUCTION_SHARED
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    //using authentication feature of firebase
    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    //providing firebaseFireStore database
    @Provides
    @Singleton
    fun provideFirebaseFireStoreDatabase() = Firebase.firestore

    @Provides
    fun introductionFragmentSharedPref(
        application : Application
    ) = application.getSharedPreferences(INTRODUCTION_SHARED,MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideFirebaseCommon(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ) = FirebaseCommon(firestore,firebaseAuth)
}