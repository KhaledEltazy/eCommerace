package com.android.ecommerce.viewmodel.cart_biling_adress_all_orders_viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.ecommerce.data.Address
import com.android.ecommerce.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _addAddress = MutableStateFlow<Resource<Address>>(Resource.Unspecified())
    val addAddress = _addAddress.asStateFlow()

    private val _deleteAddress = MutableStateFlow<Resource<Address>>(Resource.Unspecified())
    val deleteAddress = _deleteAddress.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    fun addAddress(address : Address){
        val validateInput = validateInput(address)
        if (validateInput) {
            viewModelScope.launch { _addAddress.emit(Resource.Loading()) }
            firestore.collection("user").document(auth.uid!!).collection("address").document()
                .set(address)
                .addOnSuccessListener {
                    viewModelScope.launch {
                        _addAddress.emit(Resource.Success(address))
                    }
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _addAddress.emit(Resource.Error(it.message.toString()))
                    }
                }
        } else {
            viewModelScope.launch {
                _error.emit("All Fields are required")
            }
        }
    }

    private fun validateInput(address: Address) : Boolean {
        return address.addressTitle.trim().isNotEmpty() &&
                address.fullName.trim().isNotEmpty() &&
                address.street.trim().isNotEmpty() &&
                address.phone.trim().isNotEmpty() &&
                address.city.trim().isNotEmpty() &&
                address.state.trim().isNotEmpty()
    }

    fun deleteAddress(address: Address){
        viewModelScope.launch {
            _deleteAddress.emit(Resource.Loading())
        }
        firestore.collection("user").document(auth.uid!!).collection("address")
            .whereEqualTo("addressTitle",address.addressTitle)
            .whereEqualTo("fullName",address.fullName)
            .whereEqualTo("city",address.city)
            .whereEqualTo("street",address.street)
            .whereEqualTo("phone",address.phone)
            .whereEqualTo("state",address.state)
            .get()
            .addOnSuccessListener { querySnapShot ->
                if(!querySnapShot.isEmpty){
                    for (document in querySnapShot.documents){
                        document.reference.delete()
                            .addOnSuccessListener {
                                viewModelScope.launch {
                                    _deleteAddress.emit(Resource.Success(address))
                                }
                            }.addOnFailureListener {
                                viewModelScope.launch {
                                    _deleteAddress.emit(Resource.Error(it.message.toString()))
                                }
                            }
                    }
                } else {
                    viewModelScope.launch {
                        _error.emit("Address not found")
                    }
                }
            } .addOnFailureListener {
                viewModelScope.launch {
                    _deleteAddress.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}