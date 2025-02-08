package com.android.ecommerce.fragments.shopping_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ecommerce.R
import com.android.ecommerce.adapters.BestDealsAdapter
import com.android.ecommerce.databinding.FragmentSearchBinding
import com.android.ecommerce.util.Resource
import com.android.ecommerce.util.VerticalItemDecoration
import com.android.ecommerce.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchFragment : Fragment() {
    private lateinit var binding : FragmentSearchBinding
    private val searchViewmodel by viewModels<SearchViewModel>()
    private val productAdapter by lazy{
        BestDealsAdapter()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpSearchRV()

        // Set up the search bar to update the query
        binding.tvSearchSF.addTextChangedListener { text ->
            val query = text.toString()
            searchViewmodel.searchProducts(query) // Call search function in ViewModel
        }

        //observe SearchResult
        lifecycleScope.launch {
            searchViewmodel.searchResult.collect{
                when(it){
                    is Resource.Loading ->{
                        binding.searchProgressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success ->{
                        binding.searchProgressBar.visibility = View.GONE
                        productAdapter.differ.submitList(it.data)
                    }
                    is Resource.Error ->{
                        binding.searchProgressBar.visibility = View.GONE
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setUpSearchRV() {
        binding.searchRv.apply {
            adapter = productAdapter
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            addItemDecoration(VerticalItemDecoration())
        }
    }
}