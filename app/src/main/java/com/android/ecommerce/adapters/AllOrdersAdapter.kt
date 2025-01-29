package com.android.ecommerce.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.ecommerce.R
import com.android.ecommerce.data.order.Order
import com.android.ecommerce.data.order.OrderStatus
import com.android.ecommerce.data.order.getOrderStatus
import com.android.ecommerce.databinding.OrderItemBinding

class AllOrdersAdapter : RecyclerView.Adapter<AllOrdersAdapter.AllOrdersViewHolder>() {

    inner class AllOrdersViewHolder(val binding :OrderItemBinding) : RecyclerView.ViewHolder(binding.root){

    }

    private val diffUtil = object : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return newItem.products == oldItem.products
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return newItem == oldItem
        }
    }

    val differ = AsyncListDiffer(this,diffUtil)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllOrdersViewHolder {
        return AllOrdersViewHolder(OrderItemBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        ))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: AllOrdersViewHolder, position: Int) {
        val currentOrders = differ.currentList[position]

        val resources = holder.itemView.resources

        val colorDrawable = when(getOrderStatus(currentOrders.orderStatus)){
            is OrderStatus.Ordered ->{
                ColorDrawable(resources.getColor(R.color.g_orange_yellow))
            }
            is OrderStatus.Confirmed ->{
                ColorDrawable(resources.getColor(R.color.green))
            }
            is OrderStatus.Delivered ->{
                ColorDrawable(resources.getColor(R.color.green))
            }
            is OrderStatus.Shipped ->{
                ColorDrawable(resources.getColor(R.color.green))
            }
            is OrderStatus.Cancel ->{
                ColorDrawable(resources.getColor(R.color.g_red))
            }
            is OrderStatus.Returned ->{
                ColorDrawable(resources.getColor(R.color.g_red))
            }
        }

        holder.binding.apply {
            tvOrderId.text = currentOrders.orderId.toString()
            tvOrderDate.text = currentOrders.date
            imageOrderState.setImageDrawable(colorDrawable)
        }

        holder.itemView.setOnClickListener {
            onClicked?.invoke(currentOrders)
        }
    }

    var onClicked : ((Order)-> Unit)? = null

}