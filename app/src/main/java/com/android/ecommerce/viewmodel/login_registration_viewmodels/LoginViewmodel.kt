package com.android.ecommerce.viewmodel.login_registration_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.ecommerce.util.Resource
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewmodel @Inject constructor(
    private val firebaseAuth : FirebaseAuth,
    private val googleSignInClient : GoogleSignInClient
) : ViewModel() {

    private val _login = MutableSharedFlow<Resource<FirebaseUser>>()
    val login = _login.asSharedFlow()

    private val _resetPassword = MutableSharedFlow<Resource<String>>()
    val resetPassword = _resetPassword.asSharedFlow()

    private val _googleLogin = MutableSharedFlow<Resource<FirebaseUser>>()
    val googleLogin = _googleLogin.asSharedFlow()

    //login account function
    fun loggingAccount(email : String , password : String){
        viewModelScope.launch {
            _login.emit(Resource.Loading())
        }
        firebaseAuth.signInWithEmailAndPassword(
            email,password
        ).addOnSuccessListener {
            viewModelScope.launch {
                it.user?.let {
                    _login.emit(Resource.Success(it))
                }
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _login.emit(Resource.Error(it.message.toString()))
            }
        }
    }

    //google login Function
    fun logInByGoogle(idToken : String){
        viewModelScope.launch {
            _googleLogin.emit(Resource.Loading())
        }
        val credential = GoogleAuthProvider.getCredential(idToken,null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
             viewModelScope.launch {
                 it.user?.let {
                     _googleLogin.emit(Resource.Success(it))
                 }
             }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _googleLogin.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    //handling ForgotPassword link
    fun resetPassword(email:String){
        viewModelScope.launch {
            _resetPassword.emit(Resource.Loading())
        }

            firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    viewModelScope.launch {
                        _resetPassword.emit(Resource.Success(email))
                    }
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _resetPassword.emit(Resource.Error(it.message.toString()))
                    }
        }
    }


    fun getGoogleSignInClient(): GoogleSignInClient {
        return googleSignInClient
    }

}