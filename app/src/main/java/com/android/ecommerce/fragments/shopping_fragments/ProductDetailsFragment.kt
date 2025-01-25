package com.android.ecommerce.fragments.shopping_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ecommerce.R
import com.android.ecommerce.adapters.ColorAdapter
import com.android.ecommerce.adapters.SizeAdapter
import com.android.ecommerce.adapters.Viewpager2Adapter
import com.android.ecommerce.data.CartProduct
import com.android.ecommerce.databinding.FragmentProductDetailsBinding
import com.android.ecommerce.helper.getProductPrice
import com.android.ecommerce.util.Resource
import com.android.ecommerce.util.hidingBottomNavView
import com.android.ecommerce.viewmodel.DetailsViewModel
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
    private val viewModel by viewModels<DetailsViewModel>()


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

        //determine the selectedSize
        sizeAdapter.onSizeSelected ={
            selectedSize = it
        }

        //determine the selectedColor
        colorAdapter.onColorClicked= {
            selectedColor = it
        }

        val prodcut = args.product
        binding.apply {

            ivClose.setOnClickListener {
                findNavController().navigateUp()
            }

            tvProductNameProductDetails.text = prodcut.productName
            tvPriceProductDetails.text = prodcut.offer.getProductPrice(prodcut.price).toString()

            if(prodcut.productDescription != null)
                tvProductDescriptionProductDetails.text = prodcut.productDescription
            else {
                tvProductDescriptionProductDetails.visibility = View.GONE
            }

            //implementing of addCartButton
            //checking if product has color and sizes or not
            //checking if the user select color or sizes of founded
            btnAddToCart.setOnClickListener {
                if(prodcut.sizes !=null && prodcut.color != null){
                    if(selectedSize != null && selectedColor != null){
                        viewModel.addUpdateProductInCart(CartProduct(prodcut,1,selectedColor,selectedSize))
                    } else {
                        Toast.makeText(requireContext(),"Please Select Color and Size",Toast.LENGTH_LONG).show()
                    }
                } else if (prodcut.sizes ==null && prodcut.color == null){
                    viewModel.addUpdateProductInCart(CartProduct(prodcut,1))
                } else if(prodcut.sizes != null){
                    if(selectedSize !=null){
                        viewModel.addUpdateProductInCart(CartProduct(prodcut,1,null,selectedSize))
                    } else {
                        Toast.makeText(requireContext(),"Please Select Size of the product",Toast.LENGTH_LONG).show()
                    }
                } else {
                    if(selectedColor !=null){
                        viewModel.addUpdateProductInCart(CartProduct(prodcut,1,selectedColor))
                    } else {
                        Toast.makeText(requireContext(),"Please Select color of the product",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        if (prodcut.color != null){
            colorAdapter.differ.submitList(prodcut.color)
        } else {
            binding.tvColorProductDetails.visibility = View.GONE
            binding.rvColor.visibility = View.GONE
        }

        if (prodcut.sizes != null){
            sizeAdapter.differ.submitList(prodcut.sizes)
        }else {
            binding.tvSizesProductDetails.visibility = View.GONE
            binding.rvSizes.visibility = View.GONE
        }

        viewpager2Adapter.differ.submitList(prodcut.images!!)

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