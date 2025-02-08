package com.android.ecommerce.viewmodel.categories_viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.ecommerce.data.CartProduct
import com.android.ecommerce.firebase.FirebaseCommon
import com.android.ecommerce.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    val auth : FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) : ViewModel(){

    private val _addCart = MutableStateFlow<Resource<CartProduct>>(Resource.Unspecified())
    val addCart = _addCart.asStateFlow()


    fun addUpdateProductInCart(cartProduct: CartProduct){
        viewModelScope.launch {
            _addCart.emit(Resource.Loading())
        }
        firestore.collection("user").document(auth.uid!!).collection("cart")
            .whereEqualTo("product.id",cartProduct.product.id).get()
            .addOnSuccessListener {
                it.documents.let {
                    if(it.isEmpty()){ // add new product
                        addNewProduct(cartProduct)
                    } else {
                        val product = it.first().toObject(CartProduct::class.java)
                        if(product == cartProduct){ // increase quantity
                            val id = it.first().id
                            increaseQuantity(id,product)
                        } else{ // add new product
                            addNewProduct(cartProduct)
                        }
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _addCart.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    private fun addNewProduct(cartProduct: CartProduct){
        firebaseCommon.addProductToCart(cartProduct){addProduct,e->
            viewModelScope.launch {
             if(e ==null){
                 _addCart.emit(Resource.Success(addProduct!!))
             } else{
                 _addCart.emit(Resource.Error(e.message.toString()))
             }
            }
        }
    }

    private fun increaseQuantity(documentId: String,cartProduct: CartProduct){
        firebaseCommon.increaseQuantity(documentId){_,e->
            viewModelScope.launch {
                if(e ==null){
                    _addCart.emit(Resource.Success(cartProduct))
                } else{
                    _addCart.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }
}