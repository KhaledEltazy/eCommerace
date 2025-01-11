package com.android.ecommerce.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.ecommerce.data.Product
import com.android.ecommerce.databinding.ProductRvItemBinding

class BestProductsAdapter : RecyclerView.Adapter<BestProductsAdapter.BestProductViewHolder>() {
    inner class BestProductViewHolder(private val binding : ProductRvItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(product : Product){
            binding.apply {
                //Glide.with(itemView).load(product.images[0]).into(ivBDProductImage)
                productNameTV.text=  product.productName
                productPriceTV.text = product.price.toString()
                product.offer?.let {
                    newPriceTV.text = "${Math.ceil((((1- product.offer)*product.price)*100).toDouble())/100}"
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
    }
}