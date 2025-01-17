package com.android.ecommerce.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.ecommerce.R
import com.android.ecommerce.adapters.BestProductsAdapter
import com.android.ecommerce.databinding.FragmentBaseCategoryBinding

open class BaseCategoryFragment : Fragment(R.layout.fragment_base_category) {
    private lateinit var binding : FragmentBaseCategoryBinding
    protected val offerProductAdapter : BestProductsAdapter by lazy{ BestProductsAdapter()}
    protected val bestProductsAdapter : BestProductsAdapter by lazy { BestProductsAdapter()}
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBaseCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOfferProductRV()
        setBestProductsRV()


        binding.rvBaseCategoryOfferProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private var isLoading = false

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!recyclerView.canScrollHorizontally(1) && dx > 0 && !isLoading) {
                    isLoading = true
                    onOfferPagingRequest()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    isLoading = false
                }
            }
        })

        binding.nestedScrollBaseCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{
                v,_,scrollV,_,_ ->
            if (v.getChildAt(0).bottom <= v.height + scrollV){
                onBestProductsPagingRequest()
            }
        })
    }

    open fun onOfferPagingRequest(){
        // Implementation in inherited class
    }

    open fun onBestProductsPagingRequest(){
        // Implementation in inherited class
    }

    fun showingLoadingOfferProducts(){
        binding.progressBarOfferProductsBC.visibility = View.VISIBLE
    }

    fun hideLoadingOfferProducts(){
        binding.progressBarOfferProductsBC.visibility = View.GONE
    }

    fun showingLoadingBestProducts(){
        binding.bestProductProgressBarBC.visibility = View.VISIBLE
    }

    fun hideLoadingBestProducts(){
        binding.bestProductProgressBarBC.visibility = View.GONE
    }




    private fun setBestProductsRV() {
        binding.rvBestProductBC.apply {
            layoutManager = GridLayoutManager(requireContext(),2,GridLayoutManager.VERTICAL,false)
            adapter = bestProductsAdapter
        }
    }

    private fun setOfferProductRV() {
        binding.rvBaseCategoryOfferProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = offerProductAdapter
        }
    }
}