package com.android.ecommerce.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Product(
    val id : String,
    val productName : String,
    val category : String,
    val price : Float,
    val offer : Float? = null,
    val productDescription : String? = null,
    val color : List<Int>? = null,
    val sizes : List<String>? = null,
    val images : List<String>
) : Parcelable {
    constructor() : this ("","","",0.0f,null,null,null,null, emptyList())
}