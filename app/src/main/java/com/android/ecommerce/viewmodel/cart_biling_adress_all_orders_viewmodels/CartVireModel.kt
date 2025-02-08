package com.android.ecommerce.viewmodel.cart_biling_adress_all_orders_viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.ecommerce.data.CartProduct
import com.android.ecommerce.firebase.FirebaseCommon
import com.android.ecommerce.helper.getProductPrice
import com.android.ecommerce.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth : FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) : ViewModel() {

    private val _cartProducts =
        MutableStateFlow<Resource<List<CartProduct>>>(Resource.Unspecified())
    val cartProducts = _cartProducts.asStateFlow()

    private val _deleteDialog = MutableSharedFlow<CartProduct>()
    val deleteDialog = _deleteDialog.asSharedFlow()

    private var cartProductDocument = emptyList<DocumentSnapshot>()

    //to get total price
    val productsPrice = cartProducts.map {
        when(it){
            is Resource.Success -> {
                calculatePrice(it.data!!)
            }
            else -> null
        }
    }

    init {
        getCartProduct()
    }

    private fun getCartProduct(){
        viewModelScope.launch{
            _cartProducts.emit(Resource.Loading())
        }
        firestore.collection("cart").document(auth.uid!!)
            .collection("cart")
            .addSnapshotListener { value, error ->
                if(error != null || value == null){
                    viewModelScope.launch { _cartProducts.emit(Resource.Error(error?.message.toString())) }
                } else {
                    cartProductDocument = value.documents
                    val cartProduct = value.toObjects(CartProduct::class.java)
                    viewModelScope.launch { _cartProducts.emit(Resource.Success(cartProduct)) }
                }
            }
    }

    fun changeQuantity(
        cartProduct: CartProduct,
        quantityChanging: FirebaseCommon.QuantityChanging
    ) {
        val index = cartProducts.value.data?.indexOf(cartProduct)

        /*
        index could be equals to -1 if the function getCartProduct delays
        which will also delay the result as expect to be inside the [_cartProducts]
        and to prevent app crashing we make check
        */

        if (index != -1 && index != null) {
            val documentId = cartProductDocument[index].id
            when (quantityChanging) {
                FirebaseCommon.QuantityChanging.INCREASE -> {
                    viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                    increaseQuantity(documentId)
                }
                FirebaseCommon.QuantityChanging.DECREASE -> {
                    if(cartProduct.quantity == 1){
                        viewModelScope.launch {
                            _deleteDialog.emit(cartProduct)
                        }
                        return
                    }
                    viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                    decreaseQuantity(documentId)
                }
            }
        }
    }

    private fun decreaseQuantity(documentId: String) {
        firebaseCommon.decreaseQuantity(documentId){result,exception->
            if(exception != null){
                viewModelScope.launch {
                    _cartProducts.emit(Resource.Error(exception.message.toString()))
                }
            }
        }
    }

    private fun increaseQuantity(documentId: String) {
        firebaseCommon.increaseQuantity(documentId){result,exception->
            if(exception != null){
                viewModelScope.launch {
                    _cartProducts.emit(Resource.Error(exception.message.toString()))
                }
            }
        }
    }

    private fun calculatePrice(data : List<CartProduct>) : Float{
        return data.sumByDouble {cartProduct ->
            cartProduct.product.offer.getProductPrice(cartProduct.product.price) * cartProduct.quantity.toDouble()
        }.toFloat()
    }

    //delete item function
    fun deleteCartProduct(cartProduct : CartProduct) {
       val index = cartProducts.value.data?.indexOf(cartProduct)
        if (index != null && index != -1) {
            val documentID = cartProductDocument[index].id
            firestore.collection("cart").document(auth.uid!!).collection("cart").document(documentID).delete()
        }
    }

    fun deleteAllCartProduct(){
        val userCartCollection = firestore
            .collection("cart")
            .document(auth.uid!!)
            .collection("cart")

        // Get all the documents inside the cart collection
        userCartCollection.get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot.documents) {
                // Delete each document
                document.reference.delete()
            }
        }.addOnFailureListener { exception ->
            // Handle failure (optional)
            Log.e("DeleteCart", "Error deleting cart products: ", exception)
        }

    }
}