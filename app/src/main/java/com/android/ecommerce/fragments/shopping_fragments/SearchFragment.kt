package com.android.ecommerce.fragments.shopping_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ecommerce.R
import com.android.ecommerce.adapters.BestDealsAdapter
import com.android.ecommerce.adapters.SearchAdapter
import com.android.ecommerce.databinding.FragmentSearchBinding
import com.android.ecommerce.util.Constants.PRODUCT
import com.android.ecommerce.util.Resource
import com.android.ecommerce.util.VerticalItemDecoration
import com.android.ecommerce.util.showingBottomNavView
import com.android.ecommerce.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchFragment : Fragment() {
    private lateinit var binding : FragmentSearchBinding
    private val searchViewmodel by viewModels<SearchViewModel>()
    private val productAdapter by lazy{
        SearchAdapter()
    }
    var closeClicked : Boolean = true

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

        val searchView = binding.searchView
        val searchContainer = binding.searchBarSF
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query =="" || query == null){
                    showingEmptySearch()
                    hidingProductRv()
                    closeClicked = true
                } else {
                    hidingEmptySearch()
                    showingProductRv()
                    searchViewmodel.searchProducts(query)
                    closeClicked = false
                }
                return true
            }


            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText == "" || newText == null){
                    showingEmptySearch()
                    hidingProductRv()
                    closeClicked = true
                } else {
                    hidingEmptySearch()
                    showingProductRv()
                    searchViewmodel.searchProducts(newText)
                    closeClicked = false
                }
                return true
            }
        })

        // Make entire search container clickable to activate SearchView
        searchContainer.setOnClickListener {
            searchView.isFocusable = true
            searchView.isIconified = false // Ensure SearchView is expanded
            searchView.requestFocus() // Show keyboard
        }

        // Handle clear button (Close icon) when user clicks it
        searchView.setOnCloseListener {
            if(!closeClicked) {
                showingEmptySearch()
                hidingProductRv()
                productAdapter.differ.currentList.clear()
                searchView.setQuery("", false) // Clear text
                searchView.clearFocus() // Hide keyboard
                closeClicked = true
            }
            true
        }

        //handle see productButton
        productAdapter.onClickedItem = {
            val bundle =  Bundle().apply {
                putParcelable(PRODUCT,it)
            }
            findNavController().navigate(R.id.action_searchFragment_to_productDetailsFragment,bundle)
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

    private fun showingEmptySearch(){
        binding.layoutSearchEmpty.visibility = View.VISIBLE
    }

    private fun hidingEmptySearch(){
        binding.layoutSearchEmpty.visibility = View.GONE
    }

    private fun showingProductRv(){
        binding.searchRv.visibility = View.VISIBLE
    }

    private fun hidingProductRv(){
        binding.searchRv.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        showingBottomNavView()
    }
}