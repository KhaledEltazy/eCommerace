package com.android.ecommerce.data.order

sealed class OrderStatus(val status : String) {
    object Ordered : OrderStatus("Ordered")
    object Cancel : OrderStatus("Cancel")
    object Confirmed : OrderStatus("Confirmed")
    object Shipped : OrderStatus("Shipped")
    object Delivered : OrderStatus("Delivered")
    object Returned : OrderStatus("Returned")
}