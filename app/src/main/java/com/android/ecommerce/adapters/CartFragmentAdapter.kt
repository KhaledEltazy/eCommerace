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
import com.bumptech.glide.Glide

class CartFragmentAdapter : RecyclerView.Adapter<CartFragmentAdapter.CartFragmentViewHolder>() {

    inner class CartFragmentViewHolder(val binding : CartProductItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind( cart : CartProduct){
            binding.productNameCartItemTv.text = cart.product.productName
            binding.quantityCartItemTv.text = cart.quantity.toString()
            if (cart.product.offer != null){
                binding.productPriceCartItemTv.text =
                    "${Math.ceil((((1-cart.product.offer)*cart.product.price)*100).toDouble())/100}"
            } else {
                binding.productPriceCartItemTv.text = cart.product.price.toString()
            }
            //Glide.with(itemView).load(cart.product.images[0]!!).into(binding.productImageCartItemIV)

            if(cart.selectedColor != null){
            val imageDrawable = ColorDrawable(cart.selectedColor!!)
            binding.colorIconCartItemIv.setImageDrawable(imageDrawable)
            } else {
                binding.colorIconCartItemIv.visibility = View.GONE
            }

            if(cart.selectedSize != null) {
                binding.productSizeCartItem.text = cart.selectedSize
            } else {
                binding.sizeIconCartItemIv.visibility = View.GONE
                binding.productSizeCartItem.visibility = View.GONE
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