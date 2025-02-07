package com.android.ecommerce.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.ecommerce.data.CartProduct
import com.android.ecommerce.databinding.BillingProductsRvItemBinding
import com.android.ecommerce.helper.getProductPrice
import com.bumptech.glide.Glide

class BillingProductAdapter : RecyclerView.Adapter<BillingProductAdapter.BillingViewHolder>() {
    inner class BillingViewHolder( val binding : BillingProductsRvItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(cart : CartProduct){
            binding.apply {
                tvProductCartName.text = cart.product.productName
                tvBillingProductQuantity.text = cart.quantity.toString()
                tvProductCartPrice.text = cart.product.offer.getProductPrice(cart.product.price).toString()
                Glide.with(itemView).load(cart.product.images[0]).into(imageCartProduct)

                if (cart.selectedColor != null) {
                    val imageDrawable = ColorDrawable(cart.selectedColor)
                    imageCartProductColor.setImageDrawable(imageDrawable)
                } else {
                    imageCartProductColor.visibility = View.GONE
                }

                if (cart.selectedSize != null) {
                    tvCartProductSize.text = cart.selectedSize
                } else {
                    imageCartProductSize.visibility = View.GONE
                    tvCartProductSize.visibility = View.GONE
                }
            }
        }
    }

    private val diffUntil = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return newItem == oldItem
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return newItem == oldItem
        }

    }

    val differ = AsyncListDiffer(this,diffUntil)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingViewHolder {
        return BillingViewHolder(
            BillingProductsRvItemBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        ))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BillingViewHolder, position: Int) {
        val currentAddress = differ.currentList[position]

        holder.bind(currentAddress)
    }

}