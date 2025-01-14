package com.android.ecommerce.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.ecommerce.databinding.ColorItemBinding

class ColorAdapter : RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {
    private var colorSelectedPosition = -1
    inner class ColorViewHolder(private val binding : ColorItemBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(color : Int, position: Int){
            val imageDrawable = ColorDrawable(color)
            binding.colorViewer.setImageDrawable(imageDrawable)
            if(position == colorSelectedPosition){
                binding.colorShadow.visibility = View.VISIBLE
                binding.colorChecked.visibility = View.VISIBLE
            } else {
                binding.colorShadow.visibility = View.INVISIBLE
                binding.colorChecked.visibility = View.INVISIBLE
            }
        }

    }

    private val differUtil = object : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return newItem == oldItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return newItem == oldItem
        }
    }

    val differ = AsyncListDiffer(this,differUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        return ColorViewHolder(ColorItemBinding.inflate
            (LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val color = differ.currentList[position]
        holder.bind(color,position)

        holder.itemView.setOnClickListener {
            if(colorSelectedPosition >= 0)
                notifyItemChanged(colorSelectedPosition)
            colorSelectedPosition = holder.adapterPosition
            notifyItemChanged(colorSelectedPosition)

            onColorClicked?.invoke(color)
        }
    }

    var onColorClicked : ((Int) -> Unit)? = null
}