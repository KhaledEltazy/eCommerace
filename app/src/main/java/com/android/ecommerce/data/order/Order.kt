package com.android.ecommerce.data.order

import com.android.ecommerce.data.Address
import com.android.ecommerce.data.CartProduct

data class Order(
    val orderStatus : String,
    val totalPrice : Float,
    val products : List<CartProduct>,
    val address : Address
) {
}