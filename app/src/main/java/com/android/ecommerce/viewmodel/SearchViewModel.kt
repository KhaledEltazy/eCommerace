package com.android.ecommerce.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.ecommerce.data.Product
import com.android.ecommerce.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val fireStore : FirebaseFirestore
) :ViewModel() {

    private val _searchResult = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val searchResult = _searchResult.asStateFlow()

    fun searchProducts(query : String){
        if(query.isBlank()){
            _searchResult.value = Resource.Success(emptyList())
            return
        }

        _searchResult.value = Resource.Loading()
        viewModelScope.launch {
            fireStore.collection("products")
                .get()

                .addOnSuccessListener { documents ->
                    val filteredProducts = documents.mapNotNull {
                        it.toObject(Product::class.java)
                    }.filter {
                        it.productName.contains(query, ignoreCase = true)
                        it.category.contains(query, ignoreCase = true)
                    }
                    _searchResult.value = Resource.Success(filteredProducts)
                }.addOnFailureListener {
                    _searchResult.value = Resource.Error("Failed to fetch data")
                }
        }
    }
}