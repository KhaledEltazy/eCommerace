package com.android.ecommerce.fragments.categories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.android.ecommerce.data.Category
import com.android.ecommerce.util.Resource
import com.android.ecommerce.viewmodel.BaseCategoryViewModel
import com.android.ecommerce.viewmodel.factory.BaseCategoryViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AccessoryFragment : BaseCategoryFragment() {
    @Inject
    lateinit var fireStore : FirebaseFirestore

    private val viewModel by viewModels<BaseCategoryViewModel>{
        BaseCategoryViewModelFactory(fireStore, Category.Accessory)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.offerProduct.collect{
                when(it){
                    is Resource.Loading ->{
                        showingLoadingOfferProducts()
                    }
                    is Resource.Success ->{
                        offerProductAdapter.differ.submitList(it.data)
                        hideLoadingOfferProducts()
                    }
                    is Resource.Error ->{
                        Snackbar.make(requireView(),it.message.toString(), Snackbar.LENGTH_LONG).show()
                        hideLoadingOfferProducts()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launch {
            viewModel.bestProduct.collect{
                when(it){
                    is Resource.Loading ->{
                        showingLoadingBestProducts()
                    }
                    is Resource.Success ->{
                        bestProductsAdapter.differ.submitList(it.data)
                        hideLoadingBestProducts()
                    }
                    is Resource.Error ->{
                        Snackbar.make(requireView(),it.message.toString(), Snackbar.LENGTH_LONG).show()
                        hideLoadingBestProducts()
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onOfferPagingRequest() {
        viewModel.fetchOfferProduct()
    }

    override fun onBestProductsPagingRequest() {
        viewModel.fetchBestProducts()
    }
}
