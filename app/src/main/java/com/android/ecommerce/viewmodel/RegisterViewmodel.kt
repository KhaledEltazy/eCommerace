package com.android.ecommerce.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.ecommerce.data.User
import com.android.ecommerce.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewmodel @Inject constructor(
    private val firebaseAuth : FirebaseAuth
) : ViewModel() {

    private val _register = MutableStateFlow<Resource<FirebaseUser>>(Resource.Unspecified())
    val register : Flow<Resource<FirebaseUser>> = _register

    fun createAccountWithEmailAndPassword(user: User, password: String) {
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
        }
    }


}