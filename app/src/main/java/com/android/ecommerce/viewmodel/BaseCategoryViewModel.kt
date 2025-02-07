package com.android.ecommerce.viewmodel

import androidx.lifecycle.ViewModel
import com.android.ecommerce.data.Category
import com.android.ecommerce.data.Product
import com.android.ecommerce.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BaseCategoryViewModel(
    private val fireStore: FirebaseFirestore,
    private val category: Category
) : ViewModel() {

    private val _offerProduct = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val offerProduct: StateFlow<Resource<List<Product>>> = _offerProduct

    private val _bestProduct = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProduct: StateFlow<Resource<List<Product>>> = _bestProduct

    private val pagingInfoOfferProducts = PageInfoOfferProductsCategory()
    private val pagingInfoBestProducts = PageInfoBestProductsCategory()

    private var isFetchingOffers = false
    private var isFetchingBestProducts = false

    init {
        fetchOfferProduct()
        fetchBestProducts()
    }

    fun fetchOfferProduct() {
        if (!pagingInfoOfferProducts.isPagingEndOffer && !isFetchingOffers) {
            isFetchingOffers = true
            _offerProduct.value = Resource.Loading()
            fireStore.collection("products")
                .whereEqualTo("category", category.category)
                .whereNotEqualTo("offer", null)
                .limit(pagingInfoOfferProducts.offerProductPage * 6)
                .get()
                .addOnSuccessListener { snapshot ->
                    val products = snapshot.toObjects(Product::class.java)
                    pagingInfoOfferProducts.isPagingEndOffer = products.isEmpty() || products.size < 6
                    pagingInfoOfferProducts.oldOfferProduct = products
                    pagingInfoOfferProducts.offerProductPage++
                    _offerProduct.value = Resource.Success(products)
                }.addOnFailureListener { exception ->
                    _offerProduct.value = Resource.Error(exception.message ?: "Unknown Error")
                }.addOnCompleteListener {
                    isFetchingOffers = false
                }
        }
    }

    fun fetchBestProducts() {
        if (!pagingInfoBestProducts.isPagingEnd && !isFetchingBestProducts) {
            isFetchingBestProducts = true
            _bestProduct.value = Resource.Loading()
            fireStore.collection("Products")
                .whereEqualTo("category", category.category)
                .limit(pagingInfoBestProducts.bestProductPage * 6)
                .get()
                .addOnSuccessListener { snapshot ->
                    val products = snapshot.toObjects(Product::class.java)
                    pagingInfoBestProducts.isPagingEnd = products.isEmpty() || products.size < 6
                    pagingInfoBestProducts.oldBestProduct = products
                    pagingInfoBestProducts.bestProductPage++
                    _bestProduct.value = Resource.Success(products)
                }.addOnFailureListener { exception ->
                    _bestProduct.value = Resource.Error(exception.message ?: "Unknown Error")
                }.addOnCompleteListener {
                    isFetchingBestProducts = false
                }
        }
    }
}

internal data class PageInfoOfferProductsCategory(
    var offerProductPage: Long = 1,
    var oldOfferProduct: List<Product> = emptyList(),
    var isPagingEndOffer: Boolean = false
)

internal data class PageInfoBestProductsCategory(
    var bestProductPage: Long = 1,
    var oldBestProduct: List<Product> = emptyList(),
    var isPagingEnd: Boolean = false
)