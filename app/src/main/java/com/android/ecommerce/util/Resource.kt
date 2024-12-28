package com.android.ecommerce.util

sealed class Resource<T>(data : T? = null, message : String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message : String) : Resource<T>(message = message)
    class Loading<T> : Resource<T>()
}