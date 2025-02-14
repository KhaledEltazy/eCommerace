package com.android.ecommerce.util

import android.app.Application
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudinaryApi @Inject constructor() {
    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "your-cloud-name",
            "api_key" to "your-api-key",
            "api_secret" to "your-api-secret"
        )
    )

    suspend fun uploadImage(imageByteArray: ByteArray): String {
        return withContext(Dispatchers.IO) {
            try {
                val request = cloudinary.uploader().upload(imageByteArray, ObjectUtils.emptyMap())
                request["secure_url"] as String
            } catch (e: Exception) {
                throw Exception("Image upload failed: ${e.message}")
            }
        }
    }
}