package com.android.ecommerce.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.ecommerce.data.Category
import com.android.ecommerce.viewmodel.BaseCategoryViewModel
import com.google.firebase.firestore.FirebaseFirestore

class CategoryViewModelFactory(
    val fireStore  : FirebaseFirestore,
    val category: Category
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BaseCategoryViewModel(fireStore,category) as T
    }
}