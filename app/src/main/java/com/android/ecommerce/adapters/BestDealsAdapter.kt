package com.android.ecommerce.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.ecommerce.data.Product
import com.android.ecommerce.databinding.BestDealsRvItemBinding
import com.android.ecommerce.helper.getProductPrice
import com.bumptech.glide.Glide

class BestDealsAdapter : RecyclerView.Adapter<BestDealsAdapter.BestDealsViewHolder>() {
    inner class BestDealsViewHolder(private val binding : BestDealsRvItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(product : Product){
            Glide.with(itemView).load(product.images[0]).into(binding.ivBDProductImage)
            binding.tvBDProductName.text = product.productName
            binding.tvBDOldPrice.text = product.price.toString()
            product.offer?.let {
                val price = it.getProductPrice(product.price)
                binding.tvBDNewPrice.text = price.toString()
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestDealsViewHolder {
        return BestDealsViewHolder(BestDealsRvItemBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        ))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BestDealsViewHolder, position: Int) {
        val currentProduct = differ.currentList[position]

        holder.bind(currentProduct)

        holder.itemView.setOnClickListener {
            onClickedItem?.invoke(currentProduct)
        }
    }

    var onClickedItem : ((Product) -> Unit)? = null

}