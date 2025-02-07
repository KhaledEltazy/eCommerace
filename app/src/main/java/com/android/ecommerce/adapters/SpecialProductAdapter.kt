package com.android.ecommerce.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.ecommerce.data.Product
import com.android.ecommerce.databinding.SpecialProductRvItemBinding
import com.bumptech.glide.Glide

class SpecialProductAdapter : RecyclerView.Adapter<SpecialProductAdapter.SpecialProductViewHolder>() {

    inner class SpecialProductViewHolder(private val binding : SpecialProductRvItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(product: Product){
            binding.tvProductName.text = product.productName
            binding.tvProductPrice.text = product.price.toString()
            Glide.with(itemView).load(product.images).into(binding.ivSPProductImage)
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
    }

    var onClickedItem : ((Product) -> Unit)? = null
}