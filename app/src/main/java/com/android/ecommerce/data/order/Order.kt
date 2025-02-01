package com.android.ecommerce.data.order

import android.os.Parcelable
import com.android.ecommerce.data.Address
import com.android.ecommerce.data.CartProduct
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random.Default.nextLong

@Parcelize
data class Order(
    val orderStatus : String,
    val totalPrice : Float,
    val products : List<CartProduct>,
    val address : Address,
    val date : String = SimpleDateFormat("yyy-MM-dd", Locale.ENGLISH).format(Date()),
    val orderId : Long = nextLong(0,100_000_000_000) + totalPrice.toLong()
) : Parcelable {
    constructor() : this("",0f, emptyList(),Address(),"",0)
}