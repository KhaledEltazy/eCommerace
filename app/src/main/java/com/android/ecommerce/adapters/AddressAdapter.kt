package com.android.ecommerce.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.ecommerce.R
import com.android.ecommerce.data.Address
import com.android.ecommerce.databinding.AddressRvItemBinding

class AddressAdapter : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {
    var selectedAddress = -1
    inner class AddressViewHolder( val binding : AddressRvItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(address: Address, isSelected : Boolean){
            binding.apply {
                addressBtnAddresItem.text = address.addressTitle.toString()
                if (isSelected){
                    addressBtnAddresItem.background = ColorDrawable(itemView.context.resources.getColor(R.color.g_blue))

                } else {
                    addressBtnAddresItem.background = ColorDrawable(itemView.context.resources.getColor(R.color.g_white))

                }
            }
        }
    }
    
    private val diffUntil = object : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return newItem == oldItem
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return newItem == oldItem
        }
    }

    val differ = AsyncListDiffer(this,diffUntil)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        return AddressViewHolder(AddressRvItemBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        ))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val currentAddress = differ.currentList[position]

        holder.bind(currentAddress,selectedAddress == position)

        holder.binding.addressBtnAddresItem.setOnClickListener {
            if(selectedAddress >= 0)
                notifyItemChanged(selectedAddress)
            selectedAddress = holder.adapterPosition
            notifyItemChanged(selectedAddress)
            onAddressClicked?.invoke(currentAddress)
        }

    }

    init {
        differ.addListListener { _, _ ->
            notifyItemChanged(selectedAddress)
        }
    }

    var onAddressClicked : ((Address) -> Unit)? = null
}