package com.android.ecommerce.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.ecommerce.R
import com.android.ecommerce.util.Constants.ACCOUNT_OPTION_FRAGMENT
import com.android.ecommerce.util.Constants.SHARED_PREF_CHECKING_FIRST_OPEN
import com.android.ecommerce.util.Constants.SHOPPING_ACTIVITY
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroductionViewmodel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _navigate = MutableStateFlow<Int>(0)
    val navigate : StateFlow<Int> = _navigate

    companion object{

    }

    init {
        val isButtonClicked = sharedPreferences.getBoolean(SHARED_PREF_CHECKING_FIRST_OPEN,false)
        val user = firebaseAuth.currentUser

        if (user != null){
            viewModelScope.launch {
                _navigate.emit(SHOPPING_ACTIVITY)
            }
        } else if(isButtonClicked){
            viewModelScope.launch {
                _navigate.emit(ACCOUNT_OPTION_FRAGMENT)
            }
        } else{
            Unit
        }
    }

    fun startButtonClicked(){
        sharedPreferences.edit().putBoolean(SHARED_PREF_CHECKING_FIRST_OPEN,true).apply()
    }

}