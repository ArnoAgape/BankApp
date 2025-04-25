package com.aura.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aura.databinding.ItemBalanceBinding
import com.aura.ui.domain.model.UserModel
import java.text.SimpleDateFormat
import java.util.Locale

class BalanceAdapter(private val itemClickListener: OnItemClickListener) :
    ListAdapter<UserModel, BalanceAdapter.WeatherViewHolder>(DiffCallback) {

    interface OnItemClickListener {
        fun onItemClick(item: UserModel)
    }

    class WeatherViewHolder(
        private val binding: ItemBalanceBinding,
        private val itemClickListener: OnItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormatter = SimpleDateFormat("dd/MM - HH:mm", Locale.getDefault())

        fun bind(observation: UserModel) {
            val formattedDate: String = dateFormatter.format(observation.date.time)
            binding.textViewDateTime.text = formattedDate
            binding.textViewStargazing.text = if (observation.isGoodForStargazing) "⭐️" else "☁️"
            binding.root.setOnClickListener {
                itemClickListener.onItemClick(observation)
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
                return oldItem.date == newItem.date
            }

            override fun areContentsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}