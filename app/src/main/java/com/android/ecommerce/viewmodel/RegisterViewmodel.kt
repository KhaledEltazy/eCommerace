package com.android.ecommerce.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.ecommerce.data.User
import com.android.ecommerce.util.RegisterFieldsState
import com.android.ecommerce.util.RegisterValidation
import com.android.ecommerce.util.Resource
import com.android.ecommerce.util.validateEmail
import com.android.ecommerce.util.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewmodel @Inject constructor(
    private val firebaseAuth : FirebaseAuth
) : ViewModel() {

    private val _register = MutableStateFlow<Resource<FirebaseUser>>(Resource.Unspecified())
    val register : Flow<Resource<FirebaseUser>> = _register

    //the different between channel and stateFlow is channel doesn't take any parameters
    //to checks validation
    private val _validation = Channel<RegisterFieldsState>()
    val validation = _validation.receiveAsFlow()

    fun createAccountWithEmailAndPassword(user: User, password: String) {
        if(checkValidation(user, password)){
        viewModelScope.launch {
            _register.emit(Resource.Loading())
            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener {
                    it.user?.let { user ->
                        _register.value = Resource.Success(user)
                    }
                }
                .addOnFailureListener {
                    _register.value = Resource.Error(it.message.toString())
                }
        } } else {
            val registerFieldsState = RegisterFieldsState(
                validateEmail(user.email), validatePassword(password)
            )

            viewModelScope.launch {
                _validation.send(registerFieldsState)
            }
        }
    }

    private fun checkValidation(user: User, password: String) : Boolean{
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)
        val shouldRegister = emailValidation is RegisterValidation.Success &&
                passwordValidation is RegisterValidation.Success

        return shouldRegister
    }


}