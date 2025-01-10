package com.android.ecommerce.data

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
) {
    constructor() : this ("","","",0.0f,null,null,null,null, emptyList())
}