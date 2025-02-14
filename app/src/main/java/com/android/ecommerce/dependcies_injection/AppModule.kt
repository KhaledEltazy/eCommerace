package com.android.ecommerce.dependcies_injection

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.android.ecommerce.R
import com.android.ecommerce.firebase.FirebaseCommon
import com.android.ecommerce.util.CloudinaryApi
import com.android.ecommerce.util.Constants.INTRODUCTION_SHARED
import com.cloudinary.Cloudinary
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    /*
    @Provides
    @Singleton
    fun provideStorage() = FirebaseStorage.getInstance().reference
*/

    @Provides
    @Singleton
    fun provideGoogleSignInOptions(@ApplicationContext context: Context): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    @Provides
    @Singleton
    fun provideGoogleSignInClient(
        @ApplicationContext context: Context,
        gso: GoogleSignInOptions
    ): GoogleSignInClient {
        return GoogleSignIn.getClient(context, gso)
    }

    // **Provide Cloudinary Instance**
    @Provides
    @Singleton
    fun provideCloudinaryApi(): CloudinaryApi {
        return CloudinaryApi()
    }
}