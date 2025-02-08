package com.android.ecommerce.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.ecommerce.R
import com.android.ecommerce.adapters.BestDealsAdapter
import com.android.ecommerce.adapters.BestProductsAdapter
import com.android.ecommerce.adapters.SpecialProductAdapter
import com.android.ecommerce.data.CartProduct
import com.android.ecommerce.databinding.FragmentCategoryMainBinding
import com.android.ecommerce.util.Constants.PRODUCT
import com.android.ecommerce.util.HorizontalItemDecoration
import com.android.ecommerce.util.Resource
import com.android.ecommerce.util.VerticalItemDecoration
import com.android.ecommerce.util.showingBottomNavView
import com.android.ecommerce.viewmodel.categories_viewmodel.ProductDetailsViewModel
import com.android.ecommerce.viewmodel.categories_viewmodel.MainCategoryViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainCategoryFragment : Fragment(R.layout.fragment_category_main) {
    private lateinit var binding : FragmentCategoryMainBinding
    private lateinit var specialProductAdapter: SpecialProductAdapter
    private lateinit var bestDealsAdapter : BestDealsAdapter
    private lateinit var bestProductAdapter : BestProductsAdapter
    val TAG = "MainCategoryFragment"
    private val viewmodel by viewModels<MainCategoryViewmodel>()
    private val viewModelProductDetails by viewModels<ProductDetailsViewModel>()


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
        setupBestProductRv()

        bestDealsAdapter.onClickedItem = {
            val bundle = Bundle().apply {
                putParcelable(PRODUCT,it)
            }
            findNavController().navigate(R.id.action_homeShoppingFragment_to_productDetailsFragment,bundle)
        }

        bestProductAdapter.onClickedItem = {
            val bundle = Bundle().apply {
                putParcelable(PRODUCT,it)
            }
            findNavController().navigate(R.id.action_homeShoppingFragment_to_productDetailsFragment,bundle)
        }

        specialProductAdapter.onClickedItem = {
            val bundle = Bundle().apply {
                putParcelable(PRODUCT,it)
            }
            findNavController().navigate(R.id.action_homeShoppingFragment_to_productDetailsFragment,bundle)
        }

        //handle add to cart Button
        specialProductAdapter.inCLickedButton = {
            if(it.color == null && it.sizes ==null){
                viewModelProductDetails.addUpdateProductInCart(CartProduct(it,1,null,null))
            } else {
                Toast.makeText(requireContext(),"Please Select Color and Size",Toast.LENGTH_LONG).show()
                val bundle = Bundle().apply {
                    putParcelable(PRODUCT,it)
                }
                findNavController().navigate(R.id.action_homeShoppingFragment_to_productDetailsFragment,bundle)
            }
        }

        //collect the value of addCart
        lifecycleScope.launch {
            viewModelProductDetails.addCart.collectLatest{
                when(it){
                    is Resource.Loading ->{
                        specialProductAdapter.btnAnimation = 1
                    }
                    is Resource.Success ->{
                        specialProductAdapter.btnAnimation = 2
                        Toast.makeText(requireContext(),"product add to cart successfully",Toast.LENGTH_LONG).show()
                    }
                    is Resource.Error ->{
                        specialProductAdapter.btnAnimation = 2
                        Toast.makeText(requireContext(),it.message.toString(),Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }



        //specialProducts Collecting data
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

        //bestDeals Collecting data
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

        //bestProduct collecting data
        lifecycleScope.launch {
            viewmodel.bestProducts.collect{
                when(it){
                    is Resource.Loading ->{
                        binding.bestProductProgressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success ->{
                        bestProductAdapter.differ.submitList(it.data)
                        binding.bestProductProgressBar.visibility = View.GONE
                    }
                    is Resource.Error ->{
                        binding.bestProductProgressBar.visibility = View.GONE
                        Log.e(TAG,it.message.toString())
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

        //paging for bestProducts
        binding.nestedScrollMainCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{
            v,_,scrollV,_,_ ->
            if (v.getChildAt(0).bottom <= v.height + scrollV){
                viewmodel.fetchBestProducts()
            }

        })

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
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = specialProductAdapter
            addItemDecoration(HorizontalItemDecoration())
            //paging on scrolling by RecyclerView
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    // Ensure we are scrolling right (dx > 0) and not already fetching data
                    if (dx > 0) {
                        if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                            viewmodel.fetchSpecialProduct()
                        }
                    }
                }
            })
        }
    }

    //implementing recyclerView of BestDeals
    private fun setupBestDealsRv(){
        bestDealsAdapter = BestDealsAdapter()
        binding.rvBestDealsProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = bestDealsAdapter
            addItemDecoration(HorizontalItemDecoration())
            //paging on scrolling by RecyclerView
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    // Ensure we are scrolling right (dx > 0) and not already fetching data
                    if (dx > 0) {
                        if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                            viewmodel.fetchBestDeals()
                        }
                    }
                }
            })
        }
    }

    //implementing recyclerView of BestProduct
    private fun setupBestProductRv(){
        bestProductAdapter = BestProductsAdapter()
        binding.rvBestProducts.apply {
            layoutManager = GridLayoutManager(requireContext(),2,GridLayoutManager.VERTICAL,false)
            adapter = bestProductAdapter
            addItemDecoration(VerticalItemDecoration())
        }


    }

    override fun onResume() {
        super.onResume()
        showingBottomNavView()
    }
}