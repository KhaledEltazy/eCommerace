package com.android.ecommerce.fragments.shopping_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ecommerce.R
import com.android.ecommerce.adapters.AddressAdapter
import com.android.ecommerce.adapters.BillingProductAdapter
import com.android.ecommerce.data.Address
import com.android.ecommerce.data.CartProduct
import com.android.ecommerce.data.order.Order
import com.android.ecommerce.data.order.OrderStatus
import com.android.ecommerce.databinding.FragmentBillingBinding
import com.android.ecommerce.util.Resource
import com.android.ecommerce.util.hidingBottomNavView
import com.android.ecommerce.viewmodel.cart_biling_adress_all_orders_viewmodels.BillingViewModel
import com.android.ecommerce.viewmodel.cart_biling_adress_all_orders_viewmodels.CartViewModel
import com.android.ecommerce.viewmodel.cart_biling_adress_all_orders_viewmodels.OrderViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class BillingFragment : Fragment() {

    private lateinit var binding : FragmentBillingBinding
    private val addressAdapter by lazy {
        AddressAdapter()
    }
    private val billingAdapter by lazy{
        BillingProductAdapter()
    }
    private val billingViewModel by viewModels<BillingViewModel>()
    private val cartViewModel by activityViewModels<CartViewModel>()
    private val args by navArgs<BillingFragmentArgs>()
    private var products = emptyList<CartProduct>()
    private var totalPrice = 0f

    private var selectedAddress : Address? = null
    private val orderViewModel by viewModels<OrderViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hidingBottomNavView()
        binding = FragmentBillingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAddressRv()
        setBillingProductRv()

        //to get productsOrdered list and total price
        products = args.product.toList()
        billingAdapter.differ.submitList(products)
        totalPrice = args.totalPrice

        //to get selected address
        addressAdapter.onAddressClicked = {
            selectedAddress = it
            if (!args.payment) {
                hidingBottomNavView()
                val bundle = Bundle().apply {
                    putParcelable("address", selectedAddress)
                }
                findNavController().navigate(R.id.action_billingFragment_to_addressFragment, bundle)
            }
        }

        binding.apply {
            //handle plus icon to nav to address fragment
            imageAddAddress.setOnClickListener {
                findNavController().navigate(R.id.action_billingFragment_to_addressFragment)
            }

            //showing total price
            tvTotalPrice.text = "$ ${totalPrice}"

            //handle place order button
            placeOrderBtn.setOnClickListener {
                if (selectedAddress == null){
                    Toast.makeText(requireContext(),"Please select an Address",Toast.LENGTH_LONG).show()
                return@setOnClickListener
                }
                showOrderConfirmationDialog()
                // clear cartFragment to make new Order not use old one
                cartViewModel.deleteAllCartProduct()
            }

            //check if payment true or not
            if(!args.payment){
                placeOrderBtn.visibility = View.INVISIBLE
                totalBoxContainer.visibility = View.INVISIBLE
                middleLine.visibility = View.INVISIBLE
                bottomLine.visibility = View.INVISIBLE
            }

            //handle close icon to navigateUp
            binding.imageCloseBilling.setOnClickListener {
                findNavController().navigateUp()
            }
        }

        //collect the data from order
        lifecycleScope.launch {
            orderViewModel.order.collect{
                when(it){
                    is Resource.Loading ->{
                        binding.placeOrderBtn.startAnimation()
                }
                    is Resource.Success ->{
                        binding.placeOrderBtn.revertAnimation()
                        findNavController().navigateUp()
                        Snackbar.make(requireView(),"Your order was placed",Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Error ->{
                        binding.placeOrderBtn.revertAnimation()
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
            }
            }
        }


        //collect the data from getting address
        lifecycleScope.launch {
            billingViewModel.getAddress.collect{
                when(it){
                    is Resource.Loading ->{
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }
                    is Resource.Success ->{
                        binding.progressbarAddress.visibility = View.GONE
                        addressAdapter.differ.submitList(it.data)
                    }
                    is Resource.Error ->{
                        binding.progressbarAddress.visibility = View.GONE
                        Toast.makeText(requireContext(),it.message.toString(),Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun showOrderConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setTitle("Order Items")
            setMessage("Do you want to order the current items?")
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton("Yes") { dialog, _ ->
                val order = Order(
                    OrderStatus.Ordered.status,
                    totalPrice,
                    products,
                    selectedAddress!!
                )
                orderViewModel.placeOrder(order)
                dialog.dismiss()
            }
        }
        alertDialog.create()
            .show()
    }

    private fun setBillingProductRv() {
        binding.rvProducts.apply {
            adapter = billingAdapter
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        }
    }

    private fun setAddressRv() {
        binding.rvAddress.apply {
            adapter = addressAdapter
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        }
    }
}