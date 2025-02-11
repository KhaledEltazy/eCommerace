package com.android.ecommerce.fragments.shopping_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ecommerce.adapters.BillingProductAdapter
import com.android.ecommerce.data.CartProduct
import com.android.ecommerce.data.order.OrderStatus
import com.android.ecommerce.data.order.getOrderStatus
import com.android.ecommerce.databinding.FragmentOrderDetailsBinding
import com.android.ecommerce.util.hidingBottomNavView


class OrderDetailsFragment : Fragment() {

    private lateinit var binding : FragmentOrderDetailsBinding
    private val billingAdapter by lazy {
        BillingProductAdapter()
    }

    private val ordersDetailsArgs by navArgs<OrderDetailsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hidingBottomNavView()
        binding = FragmentOrderDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBillingProductRv()

        val order = ordersDetailsArgs.order

        binding.apply {
            tvOrderId.text = "Orders ${order.orderId}"

            //set upStepView
            stepView.setSteps(
                mutableListOf(
                    OrderStatus.Ordered.status,
                    OrderStatus.Confirmed.status,
                    OrderStatus.Shipped.status,
                    OrderStatus.Delivered.status
                )
            )

            val currentOrderState = when(getOrderStatus(order.orderStatus)){
                is OrderStatus.Ordered -> 0
                is OrderStatus.Confirmed -> 1
                is OrderStatus.Shipped -> 2
                is OrderStatus.Delivered -> 3
                else -> 0
            }

            //get the currentOrderState
            stepView.go(
                currentOrderState,false
            )
            if(currentOrderState == 3){
                stepView.done(true)
            }

            //get address and full name phone number
            tvFullName.text = order.address.fullName
            tvAddress.text = "${order.address.street} ${order.address.city}"
            tvPhoneNumber.text = order.address.phone
            tvTotalPrice.text = "$ ${order.totalPrice}"

            //handle close icon
            imageCloseOrder.setOnClickListener {
                findNavController().navigateUp()
            }
        }

        //to get productsOrdered list and total price
        billingAdapter.differ.submitList(order.products)


    }

    private fun setBillingProductRv() {
        binding.rvProducts.apply {
            adapter = billingAdapter
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        }
    }
}