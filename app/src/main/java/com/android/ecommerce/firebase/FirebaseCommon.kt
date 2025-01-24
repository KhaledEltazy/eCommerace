package com.android.ecommerce.firebase

import com.android.ecommerce.data.CartProduct
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseCommon (
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private val cartCollection = firestore.collection("cart").document(auth.uid!!)
        .collection("cart")

    fun addProductToCart(cartProduct: CartProduct,onResult : (CartProduct?,Exception?)-> Unit) {
        cartCollection.document().set(cartProduct)
            .addOnSuccessListener {
                onResult(cartProduct, null)
            }.addOnFailureListener {
                onResult(null, it)
            }
    }

    fun increaseQuantity(documentId : String, onResult : (String?, Exception?)->Unit){
        firestore.runTransaction {transaction->
            val documentRef = cartCollection.document(documentId)
            val document = transaction.get(documentRef)
            val productObject = document.toObject(CartProduct::class.java)
            productObject?.let { cartProduct->
                val quantity = cartProduct.quantity +1
                val newProductObject = cartProduct.copy(quantity = quantity)
                transaction.set(documentRef,newProductObject)
            }
        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null,it)
        }
    }

    fun decreaseQuantity(documentId : String, onResult : (String?, Exception?)->Unit){
        firestore.runTransaction {transaction->
            val documentRef = cartCollection.document(documentId)
            val document = transaction.get(documentRef)
            val productObject = document.toObject(CartProduct::class.java)
            productObject?.let { cartProduct->
                if(cartProduct.quantity > 1) {
                    val quantity = cartProduct.quantity - 1
                    val newProductObject = cartProduct.copy(quantity = quantity)
                    transaction.set(documentRef, newProductObject)
                }
            }
        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null,it)
        }
    }

    enum class QuantityChanging{
        INCREASE, DECREASE
    }
}