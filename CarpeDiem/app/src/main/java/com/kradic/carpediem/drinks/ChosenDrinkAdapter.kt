package com.kradic.carpediem.drinks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kradic.carpediem.databinding.ListItemChosenDrinkBinding

//Adapter za RecyclerView koji prikazuje odabrana pića
class ChosenDrinkAdapter : ListAdapter<ChosenDrink, ChosenDrinkAdapter.ViewHolder>(ChosenDrinkDiffCallBack()){

    override fun onBindViewHolder(holder: ChosenDrinkAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChosenDrinkAdapter.ViewHolder {
       return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ListItemChosenDrinkBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: ChosenDrink){
            binding.chosenDrink = item
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup) : ViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemChosenDrinkBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

}

class ChosenDrinkDiffCallBack : DiffUtil.ItemCallback<ChosenDrink>() {
    override fun areItemsTheSame(oldItem: ChosenDrink, newItem: ChosenDrink): Boolean {
        return oldItem.drinkName == newItem.drinkName
    }

    override fun areContentsTheSame(oldItem: ChosenDrink, newItem: ChosenDrink): Boolean {
        return oldItem == newItem
    }

}
