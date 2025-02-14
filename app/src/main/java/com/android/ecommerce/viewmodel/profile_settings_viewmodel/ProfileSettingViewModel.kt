package com.android.ecommerce.viewmodel.profile_settings_viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.ecommerce.data.User
import com.android.ecommerce.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileSettingViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val sharedPreferences: android.content.SharedPreferences
) : ViewModel() {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    private val _isNotificationsEnabled = MutableStateFlow(sharedPreferences.getBoolean("notifications_enabled", true))
    val isNotificationsEnabled = _isNotificationsEnabled.asStateFlow()

    init {
        getUser()
    }

    fun getUser() {
        viewModelScope.launch {
            _user.emit(Resource.Loading())
        }
        firestore.collection("user").document(auth.uid!!).addSnapshotListener { value, error ->
            if (error != null) {
                viewModelScope.launch {
                    _user.emit(Resource.Error(error.message.toString()))
                }
            } else {
                val user = value?.toObject(User::class.java)
                user?.let {
                    viewModelScope.launch {
                        _user.emit(Resource.Success(user))
                    }
                }
            }
        }
    }

    fun logout(){
        auth.signOut()
    }

    fun setNotificationPreference(isEnabled: Boolean) {
        sharedPreferences.edit().putBoolean("notifications_enabled", isEnabled).apply()
        _isNotificationsEnabled.value = isEnabled

        if (isEnabled) {
            FirebaseMessaging.getInstance().subscribeToTopic("notifications")
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("notifications")
        }
    }
}