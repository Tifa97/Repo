package com.kradic.carpediem.drinks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import com.kradic.carpediem.database.Drink
import com.kradic.carpediem.databinding.ListItemDrinkBinding
import com.kradic.carpediem.drinks.DrinkAdapter.ViewHolder

//Adapter za RecyclerView koji prikazuje pića za kategoriju
class DrinkAdapter(val clickListener: DrinkListener) : ListAdapter<Drink, ViewHolder>(DrinkDiffCallBack()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ListItemDrinkBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(item: Drink, clickListener: DrinkListener){
            binding.drink = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup) : ViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemDrinkBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }
}

//Klik listener za piće
class DrinkListener(val clickListener: (drinkId: Long) -> Unit) {
    fun onClick(drink: Drink) = clickListener(drink.drinkId)
}

class DrinkDiffCallBack : DiffUtil.ItemCallback<Drink>() {
    override fun areItemsTheSame(oldItem: Drink, newItem: Drink): Boolean {
        return oldItem.drinkId == newItem.drinkId
    }

    override fun areContentsTheSame(oldItem: Drink, newItem: Drink): Boolean {
        return oldItem == newItem
    }

}