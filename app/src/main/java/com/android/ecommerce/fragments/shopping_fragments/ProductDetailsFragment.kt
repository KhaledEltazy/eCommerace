package com.android.ecommerce.fragments.shopping_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ecommerce.adapters.ColorAdapter
import com.android.ecommerce.adapters.SizeAdapter
import com.android.ecommerce.adapters.Viewpager2Adapter
import com.android.ecommerce.databinding.FragmentProductDetailsBinding
import com.android.ecommerce.util.hidingBottomNavView


class ProductDetailsFragment : Fragment() {
    private lateinit var binding : FragmentProductDetailsBinding
    private val viewpager2Adapter by lazy { Viewpager2Adapter() }
    private val colorAdapter by lazy {ColorAdapter()}
    private val sizeAdapter by lazy { SizeAdapter()}
    private val args by  navArgs<ProductDetailsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hidingBottomNavView()
        binding = FragmentProductDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewPager()
        setColorRecyclerView()
        setSizeRecyclerView()

        val prodcut = args.product
        binding.apply {

            ivClose.setOnClickListener {
                findNavController().navigateUp()
            }

            tvProductNameProductDetails.text = prodcut.productName
            tvPriceProductDetails.text = prodcut.price.toString()

            if(prodcut.productDescription != null)
                tvProductDescriptionProductDetails.text = prodcut.productDescription
            else {
                tvProductDescriptionProductDetails.visibility = View.GONE
            }

            if (prodcut.color !=null){
                colorAdapter.differ.submitList(prodcut.color)
            } else {
                tvColorProductDetails.visibility = View.GONE
                rvColor.visibility = View.GONE
            }

            if (prodcut.sizes != null){
                sizeAdapter.differ.submitList(prodcut.sizes)
            }else {
                tvSizesProductDetails.visibility = View.GONE
                rvSizes.visibility = View.GONE
            }
        }

        viewpager2Adapter.differ.submitList(prodcut.images!!)

    }

    private fun setViewPager(){
        binding.viewpagerProductImages.apply {
            adapter = viewpager2Adapter
        }
    }

    private fun setColorRecyclerView(){
        binding.rvColor.apply {
            adapter = colorAdapter
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        }
    }
    private fun setSizeRecyclerView(){
        binding.rvSizes.apply {
            adapter = sizeAdapter
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        }
    }
}