package com.aura.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aura.databinding.ItemBalanceBinding
import com.aura.ui.domain.model.UserModel

class BalanceAdapter(private val itemClickListener: OnItemClickListener) :
    ListAdapter<UserModel, BalanceAdapter.WeatherViewHolder>(DiffCallback) {

    interface OnItemClickListener {
        fun onItemClick(item: UserModel)
    }

    class WeatherViewHolder(
        private val binding: ItemBalanceBinding,
        private val itemClickListener: OnItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: UserModel) {
            binding.title.text = user.id
            binding.balance.text = user.balance.toString()
            binding.root.setOnClickListener {
                itemClickListener.onItemClick(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val itemView = ItemBalanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeatherViewHolder(itemView, itemClickListener)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<UserModel>() {
            override fun areItemsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}