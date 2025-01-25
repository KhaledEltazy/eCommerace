package com.android.ecommerce.fragments.shopping_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ecommerce.R
import com.android.ecommerce.adapters.CartFragmentAdapter
import com.android.ecommerce.data.CartProduct
import com.android.ecommerce.databinding.FragmentCartBinding
import com.android.ecommerce.firebase.FirebaseCommon
import com.android.ecommerce.util.Constants.PRODUCT
import com.android.ecommerce.util.Resource
import com.android.ecommerce.util.VerticalItemDecoration
import com.android.ecommerce.viewmodel.CartViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class CartFragment : Fragment() {
    private lateinit var binding : FragmentCartBinding
    private val cartAdapter by lazy{
        CartFragmentAdapter()
    }
    //get total price
    var totalPrice = 0f
    private val viewModel by activityViewModels<CartViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCartRv()



        //handle check Out Button
        binding.btnCheckOut.setOnClickListener{
            val action = CartFragmentDirections.actionCartFragmentToBillingFragment(totalPrice,cartAdapter.differ.currentList.toTypedArray())
            findNavController().navigate(action)
        }

        //to get total price
        lifecycleScope.launch {
            viewModel.productsPrice.collectLatest { price ->
                price?.let {
                    totalPrice = it
                    binding.totalPriceCartFragment.text = "$ ${price}"
                }
            }
        }

        //handle clicking in cartItem
        cartAdapter.onClickedItem = {
            val bundle = Bundle().apply { putParcelable(PRODUCT,it.product)}
            findNavController().navigate(R.id.action_cartFragment_to_productDetailsFragment,bundle)
        }

        //handle increase and decrease
        cartAdapter.onPlusIconSelected = {
            viewModel.changeQuantity(it,FirebaseCommon.QuantityChanging.INCREASE)
        }

        cartAdapter.onMinusIconSelected = {
            viewModel.changeQuantity(it,FirebaseCommon.QuantityChanging.DECREASE)
        }

        //handle deleteIcon
        cartAdapter.onDeleteIconClicked = {
            deleteDialog(it)
        }

        //handle DeleteAll
        binding.deleteAllCV.setOnClickListener {
            val alertDialog = AlertDialog.Builder(requireContext()).apply {
                setTitle("Delete item from cart")
                setMessage("Do you want to delete this item from cart")
                setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                setPositiveButton("Yes") { dialog, _ ->
                    viewModel.deleteAllCartProduct()
                    dialog.dismiss()
                }
            }
            alertDialog.create()
                .show()
        }

        //collect data of deleteDialog
        lifecycleScope.launch {
            viewModel.deleteDialog.collectLatest {
                deleteDialog(it)
            }
        }

        lifecycleScope.launch {
            viewModel.cartProducts.collect{
                when(it){
                    is Resource.Success ->{
                        hidingLoading()
                        if (it.data!!.isEmpty()){
                            showEmptyCart()
                            hideOtherViews()
                        } else{
                            showOtherViews()
                            hideEmptyCart()
                            cartAdapter.differ.submitList(it.data)
                        }
                    }
                    is Resource.Error ->{
                        hidingLoading()
                        Snackbar.make(requireView(),it.message.toString(),Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Loading ->{
                        showingLoading()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun deleteDialog(cartProduct: CartProduct) {
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setTitle("Delete item from cart")
            setMessage("Do you want to delete this item from cart")
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton("Yes") { dialog, _ ->
                viewModel.deleteCartProduct(cartProduct)
                dialog.dismiss()
            }
        }
        alertDialog.create()
            .show()
    }


    private fun showOtherViews() {
        binding.apply {
            rvCartFragment.visibility = View.VISIBLE
            totalConstraintLayout.visibility = View.VISIBLE
            btnCheckOut.visibility = View.VISIBLE
            deleteAllCV.visibility = View.VISIBLE
        }
    }

    private fun hideOtherViews() {
        binding.apply {
            rvCartFragment.visibility = View.GONE
            totalConstraintLayout.visibility = View.GONE
            btnCheckOut.visibility = View.GONE
            deleteAllCV.visibility = View.GONE
        }
    }

    private fun showEmptyCart(){
        binding.apply {
            layoutCartEmpty.visibility = View.VISIBLE
        }
    }

    private fun hideEmptyCart() {
        binding.apply {
            layoutCartEmpty.visibility = View.GONE
        }
    }

    private fun showingLoading(){
        binding.progressBarCart.visibility = View.VISIBLE
    }

    private fun hidingLoading(){
        binding.progressBarCart.visibility = View.GONE
    }

    private fun setCartRv(){
        binding.rvCartFragment.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            addItemDecoration(VerticalItemDecoration())
        }
    }
}