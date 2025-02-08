package com.android.ecommerce.fragments.shopping_fragments

import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ecommerce.adapters.ColorAdapter
import com.android.ecommerce.adapters.SizeAdapter
import com.android.ecommerce.adapters.Viewpager2Adapter
import com.android.ecommerce.data.CartProduct
import com.android.ecommerce.databinding.FragmentProductDetailsBinding
import com.android.ecommerce.helper.getProductPrice
import com.android.ecommerce.util.Resource
import com.android.ecommerce.util.hidingBottomNavView
import com.android.ecommerce.viewmodel.categories_viewmodel.ProductDetailsViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {
    private lateinit var binding : FragmentProductDetailsBinding
    private val viewpager2Adapter by lazy { Viewpager2Adapter() }
    private val colorAdapter by lazy {ColorAdapter()}
    private val sizeAdapter by lazy { SizeAdapter()}
    private val args by  navArgs<ProductDetailsFragmentArgs>()
    private var selectedColor : Int? = null
    private var selectedSize : String? = null
    private val viewModel by viewModels<ProductDetailsViewModel>()


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

        //receive selected Product
        val product = args.product

        //check if the product have color or not and submit color list to adapter
        if (product.color != null){
            colorAdapter.differ.submitList(product.color)
        } else {
            binding.tvColorProductDetails.visibility = View.GONE
            binding.rvColor.visibility = View.GONE
        }

        //check if the product have size or not and submit sizes list to adapter
        if (product.sizes != null){
            sizeAdapter.differ.submitList(product.sizes)
        }else {
            binding.tvSizesProductDetails.visibility = View.GONE
            binding.rvSizes.visibility = View.GONE
        }

        //submit images list to viewpagerAdapter
        viewpager2Adapter.differ.submitList(product.images)
        //connect tabLayout to viewPager to show dots under photos
        TabLayoutMediator(binding.tabLayoutDots,binding.viewpagerProductImages){_,_->}.attach()

        //determine the selectedSize
        sizeAdapter.onSizeSelected ={
            selectedSize = it
        }

        //determine the selectedColor
        colorAdapter.onColorClicked= {
            selectedColor = it
        }


        binding.apply {

            ivClose.setOnClickListener {
                findNavController().navigateUp()
            }

            tvProductNameProductDetails.text = product.productName
            if (product.offer == null) {
                tvPriceProductDetails.text = "$ ${product.price}"
                tvOfferProductDetails.visibility = View.INVISIBLE
            } else {
                tvPriceProductDetails.text = "$ ${product.price}"
                tvPriceProductDetails.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                tvOfferProductDetails.text = "$ ${product.offer.getProductPrice(product.price)}"

            }

            if(product.productDescription != null)
                tvProductDescriptionProductDetails.text = product.productDescription
            else {
                tvProductDescriptionProductDetails.visibility = View.GONE
            }

            //implementing of addCartButton
            //checking if product has color and sizes or not
            //checking if the user select color or sizes of founded
            btnAddToCart.setOnClickListener {
                if(product.sizes !=null && product.color != null){
                    if(selectedSize != null && selectedColor != null){
                        viewModel.addUpdateProductInCart(CartProduct(product,1,selectedColor,selectedSize))
                    } else {
                        Toast.makeText(requireContext(),"Please Select Color and Size",Toast.LENGTH_LONG).show()
                    }
                } else if (product.sizes ==null && product.color == null){
                    viewModel.addUpdateProductInCart(CartProduct(product,1))
                } else if(product.sizes != null){
                    if(selectedSize !=null){
                        viewModel.addUpdateProductInCart(CartProduct(product,1,null,selectedSize))
                    } else {
                        Toast.makeText(requireContext(),"Please Select Size of the product",Toast.LENGTH_LONG).show()
                    }
                } else {
                    if(selectedColor !=null){
                        viewModel.addUpdateProductInCart(CartProduct(product,1,selectedColor))
                    } else {
                        Toast.makeText(requireContext(),"Please Select color of the product",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }


        //collect the value of addCart
        lifecycleScope.launch {
            viewModel.addCart.collectLatest{
                when(it){
                    is Resource.Loading ->{
                        binding.btnAddToCart.startAnimation()
                    }
                    is Resource.Success ->{
                        binding.btnAddToCart.revertAnimation()
                        Toast.makeText(requireContext(),"product add to cart successfully",Toast.LENGTH_LONG).show()
                    }
                    is Resource.Error ->{
                        binding.btnAddToCart.revertAnimation()
                        Toast.makeText(requireContext(),it.message.toString(),Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

    }

    private fun setViewPager(){
        binding.viewpagerProductImages.apply {
            adapter = viewpager2Adapter
        }
    }

    private fun setColorRecyclerView(){
        binding.rvColor.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = colorAdapter
        }
    }

    private fun setSizeRecyclerView(){
        binding.rvSizes.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = sizeAdapter
        }
    }
}