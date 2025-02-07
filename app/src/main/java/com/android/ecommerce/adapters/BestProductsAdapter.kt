package com.android.ecommerce.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.ecommerce.data.Product
import com.android.ecommerce.databinding.ProductRvItemBinding
import com.android.ecommerce.helper.getProductPrice
import com.bumptech.glide.Glide

class BestProductsAdapter : RecyclerView.Adapter<BestProductsAdapter.BestProductViewHolder>() {
    inner class BestProductViewHolder(private val binding : ProductRvItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(product : Product){
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imageProduct)
                productNameTV.text=  product.productName
                productPriceTV.text = "$ ${product.price}"
                productPriceTV.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                product.offer?.let {
                    newPriceTV.text = "$ ${it.getProductPrice(product.price)}"
                }
            }
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestProductViewHolder {
        return BestProductViewHolder(ProductRvItemBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        ))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BestProductViewHolder, position: Int) {
        val currentProduct = differ.currentList[position]

        holder.bind(currentProduct)

        holder.itemView.setOnClickListener {
            onClickedItem?.invoke(currentProduct)
        }
    }

    var onClickedItem : ((Product) -> Unit)? = null
}