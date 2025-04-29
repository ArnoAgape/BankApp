package com.aura.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aura.R
import com.aura.databinding.ItemBalanceBinding
import com.aura.ui.domain.model.UserModel

class BalanceAdapter() :
    ListAdapter<UserModel, BalanceAdapter.WeatherViewHolder>(DiffCallback) {

    class WeatherViewHolder(
        private val binding: ItemBalanceBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(user: UserModel) {
            val balance = user.balance.toString()
            if (user.main) binding.account.setText(R.string.main) else binding.account.setText(R.string.secondary)
            binding.balance.text = "$balance €"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val itemView = ItemBalanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeatherViewHolder(itemView)
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