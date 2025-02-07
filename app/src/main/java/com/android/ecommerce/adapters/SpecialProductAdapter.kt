package com.android.ecommerce.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.ecommerce.data.Product
import com.android.ecommerce.databinding.SpecialProductRvItemBinding
import com.android.ecommerce.helper.getProductPrice
import com.bumptech.glide.Glide

class SpecialProductAdapter : RecyclerView.Adapter<SpecialProductAdapter.SpecialProductViewHolder>() {

    inner class SpecialProductViewHolder(val binding : SpecialProductRvItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.apply {
                tvProductName.text = product.productName
                Glide.with(itemView).load(product.images[0]).into(ivSPProductImage)
                if (product.offer == null) {
                    tvProductPrice.text = "$ ${product.price}"
                    tvProductOffer.visibility = View.INVISIBLE
                } else {
                    tvProductPrice.text = "$ ${product.price}"
                    tvProductPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tvProductOffer.text = "$ ${product.offer.getProductPrice(product.price)}"

                }
            }
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialProductViewHolder {
        return SpecialProductViewHolder(
            SpecialProductRvItemBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            ))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: SpecialProductViewHolder, position: Int) {
        val currentList = differ.currentList[position]

        holder.bind(currentList)

        holder.itemView.setOnClickListener {
            onClickedItem?.invoke(currentList)
        }

        holder.binding.btnAddToCart.setOnClickListener {
            inCLickedButton?.invoke(currentList)
        }

        when(btnAnimation){
            1 -> {
                holder.binding.btnAddToCart.startAnimation()
            }
            2 -> {
                holder.binding.btnAddToCart.revertAnimation()
            }
            else -> Unit
        }
    }

    var onClickedItem : ((Product) -> Unit)? = null
    var inCLickedButton : ((Product) -> Unit)? = null
    var btnAnimation : Int? = null
}