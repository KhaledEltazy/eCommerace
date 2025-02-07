package com.android.ecommerce.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.ecommerce.data.Product
import com.android.ecommerce.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewmodel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _specialProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProduct : StateFlow<Resource<List<Product>>> = _specialProducts

    private val _bestDeals = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestDeals : StateFlow<Resource<List<Product>>> = _bestDeals

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts : StateFlow<Resource<List<Product>>> = _bestProducts

    private val pagingInfo = PageInfo()
    private val pagingInfoBestDeals = PageInfoBestDeals()
    private val pagingInfoSpecialProducts = PageInfoSpecialProducts()

    init {
        fetchSpecialProduct()
        fetchBestDeals()
        fetchBestProducts()
    }

    fun fetchSpecialProduct() {
        if (!pagingInfoSpecialProducts.isPagingEndSpacialProducts) {
            viewModelScope.launch {
                _specialProducts.emit(Resource.Loading())
            }
            //
            firestore.collection("products")
                .whereNotEqualTo("sizes",null)
                .limit(pagingInfoSpecialProducts.specialProductsPage * 6)
                .get()
                .addOnSuccessListener { result ->
                    val specialProductList = result.toObjects(Product::class.java)
                    pagingInfoSpecialProducts.isPagingEndSpacialProducts = specialProductList == pagingInfoSpecialProducts.oldSpecialProducts
                    pagingInfoSpecialProducts.oldSpecialProducts = specialProductList
                    viewModelScope.launch {
                        _specialProducts.emit(Resource.Success(specialProductList))
                    }
                    pagingInfoSpecialProducts.specialProductsPage ++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _specialProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

    fun fetchBestDeals(){
        if (!pagingInfoBestDeals.isPagingEndBestDeals) {
            viewModelScope.launch {
                _bestDeals.emit(Resource.Loading())
            }
            firestore.collection("products")
                .whereNotEqualTo("offer", null)
                .limit(pagingInfoBestDeals.bestDealsPage * 6)
                .get()
                .addOnSuccessListener { result ->
                    val bestDealsList = result.toObjects(Product::class.java)
                    pagingInfoBestDeals.isPagingEndBestDeals = bestDealsList == pagingInfoBestDeals.oldBestDeals
                    pagingInfoBestDeals.oldBestDeals = bestDealsList
                    viewModelScope.launch {
                        _bestDeals.emit(Resource.Success(bestDealsList))
                    }
                    pagingInfoBestDeals.bestDealsPage ++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _bestDeals.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

     fun fetchBestProducts(){
         if (!pagingInfo.isPagingEnd){
        viewModelScope.launch {
            _bestProducts.emit(Resource.Loading())
        }
        firestore.collection("products").limit(pagingInfo.bestProductPage * 3)
            .get()
            .addOnSuccessListener { result->
                val bestProductList = result.toObjects(Product::class.java)
                pagingInfo.isPagingEnd = bestProductList == pagingInfo.oldBestProduct
                pagingInfo.oldBestProduct = bestProductList
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Success(bestProductList))
                }
                pagingInfo.bestProductPage++
            }.addOnFailureListener {
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }
     }


}

internal data class PageInfo(
     var bestProductPage : Long = 1,
    var oldBestProduct : List<Product> = emptyList(),
    var isPagingEnd : Boolean = false
){
}

internal data class PageInfoBestDeals(
    var bestDealsPage : Long = 1,
    var oldBestDeals : List<Product> = emptyList(),
    var isPagingEndBestDeals : Boolean = false
){
}

internal data class PageInfoSpecialProducts(
    var specialProductsPage : Long = 1,
    var oldSpecialProducts : List<Product> = emptyList(),
    var isPagingEndSpacialProducts : Boolean = false
){
}

