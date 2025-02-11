package com.android.ecommerce.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.ecommerce.data.CartProduct
import com.android.ecommerce.data.Product
import com.android.ecommerce.databinding.CartProductItemBinding
import com.android.ecommerce.helper.getProductPrice
import com.bumptech.glide.Glide

class CartFragmentAdapter : RecyclerView.Adapter<CartFragmentAdapter.CartFragmentViewHolder>() {

    inner class CartFragmentViewHolder(val binding : CartProductItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cart: CartProduct) {
            binding.apply {
                productNameCartItemTv.text = cart.product.productName
                quantityCartItemTv.text = cart.quantity.toString()
                Glide.with(itemView).load(cart.product.images[0]).into(productImageCartItemIV)

                if(cart.product.offer == null){
                    productPriceCartItemTv.text = cart.product.price.toString()
                } else {
                    productPriceCartItemTv.text = cart.product.offer.getProductPrice(cart.product.price).toString()
                }

                if (cart.selectedColor != null) {
                    val imageDrawable = ColorDrawable(cart.selectedColor)
                    colorIconCartItemIv.setImageDrawable(imageDrawable)
                } else {
                    colorIconCartItemIv.visibility = View.GONE
                }

                if (cart.selectedSize != null) {
                    productSizeCartItem.text = cart.selectedSize
                } else {
                    sizeIconCartItemIv.visibility = View.GONE
                    productSizeCartItem.visibility = View.GONE
                }
            }
        }
    }

    val diffUtil = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return newItem == oldItem
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return newItem == oldItem
        }
    }

    val differ = AsyncListDiffer(this,diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartFragmentViewHolder {
        return CartFragmentViewHolder(
            CartProductItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: CartFragmentViewHolder, position: Int) {
        val currentCartList = differ.currentList[position]

        holder.bind(currentCartList)

        holder.itemView.setOnClickListener {
            onClickedItem?.invoke(currentCartList)
        }

        holder.binding.plusQuantityCartItemIv.setOnClickListener {
            onPlusIconSelected?.invoke(currentCartList)
        }
        holder.binding.minusQuantityCartItemIv.setOnClickListener {
            onMinusIconSelected?.invoke(currentCartList)
        }

        holder.binding.deleteCart.setOnClickListener {
            onDeleteIconClicked?.invoke(currentCartList)
        }
    }

    var onClickedItem : ((CartProduct) -> Unit)? = null
    var onPlusIconSelected : ((CartProduct) -> Unit)? = null
    var onMinusIconSelected : ((CartProduct) -> Unit)? = null
    var onDeleteIconClicked : ((CartProduct) -> Unit)? = null

}