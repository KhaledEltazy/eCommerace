package com.android.ecommerce.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.ecommerce.data.Product
import com.android.ecommerce.databinding.SearchItemBinding
import com.android.ecommerce.helper.getProductPrice
import com.bumptech.glide.Glide

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {
    inner class SearchViewHolder(val binding : SearchItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(ivBDProductImage)
                tvBDProductName.text = product.productName
                if (product.offer == null) {
                    tvPriceBestDeals.text = "$ ${product.price}"
                    tvOfferBestDeals.visibility = View.INVISIBLE
                } else {
                    tvPriceBestDeals.text = "$ ${product.price}"
                    tvPriceBestDeals.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tvOfferBestDeals.text = "$ ${product.offer.getProductPrice(product.price)}"

                }
            }
        }
    }
    private val diffResult = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,diffResult)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(
            SearchItemBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        ))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val currentProduct = differ.currentList[position]

        holder.bind(currentProduct)

        holder.binding.btnSeeProduct.setOnClickListener {
            onClickedItem?.invoke(currentProduct)
        }
    }

    var onClickedItem : ((Product) -> Unit)? = null

}