package com.android.ecommerce.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ecommerce.R
import com.android.ecommerce.adapters.BestDealsAdapter
import com.android.ecommerce.adapters.SpecialProductAdapter
import com.android.ecommerce.databinding.FragmentCategoryMainBinding
import com.android.ecommerce.util.Resource
import com.android.ecommerce.viewmodel.MainCategoryViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainCategoryFragment : Fragment(R.layout.fragment_category_main) {
    private lateinit var binding : FragmentCategoryMainBinding
    private lateinit var specialProductAdapter: SpecialProductAdapter
    private lateinit var bestDealsAdapter : BestDealsAdapter
    val TAG = "MainCategoryFragment"
    private val viewmodel by viewModels<MainCategoryViewmodel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentCategoryMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpecialProductRv()
        setupBestDealsRv()

        lifecycleScope.launch {
            viewmodel.specialProduct.collect{
                when(it){
                    is Resource.Loading ->{
                        showLoading()
                    }
                    is Resource.Success ->{
                        specialProductAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error ->{
                        hideLoading()
                        Log.e(TAG,it.message.toString())
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launch {
            viewmodel.bestDeals.collect{
                when(it){
                    is Resource.Loading ->{
                        showLoading()
                    }
                    is Resource.Success ->{
                        bestDealsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error ->{
                        Log.e(TAG,it.message.toString())
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
                        hideLoading()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun showLoading(){
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading(){
        binding.progressBar.visibility = View.GONE
    }

    //implementing recyclerView of specialProduct
    private fun setupSpecialProductRv(){
        specialProductAdapter = SpecialProductAdapter()
        binding.rvSpecialProduct.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = specialProductAdapter
        }
    }

    //implementing recyclerView of BestDeals
    private fun setupBestDealsRv(){
        bestDealsAdapter = BestDealsAdapter()
        binding.rvBestDealsProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            adapter = bestDealsAdapter
        }
    }
}