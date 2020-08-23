package com.kradic.carpediem.dailyIncome

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kradic.carpediem.database.SoldDrink
import com.kradic.carpediem.databinding.ListItemSoldDrinkBinding

//Adapter za RecyclerView koji prikazuje prodana pića
class SoldDrinkAdapter : ListAdapter<SoldDrink, SoldDrinkAdapter.ViewHolder>(SoldDrinkDiffCallBack()){

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ListItemSoldDrinkBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: SoldDrink){
            binding.soldDrink = item
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup) : ViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemSoldDrinkBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class SoldDrinkDiffCallBack : DiffUtil.ItemCallback<SoldDrink>() {
    override fun areItemsTheSame(oldItem: SoldDrink, newItem: SoldDrink): Boolean {
        return oldItem.soldDrinkId == newItem.soldDrinkId
    }

    override fun areContentsTheSame(oldItem: SoldDrink, newItem: SoldDrink): Boolean {
        return oldItem == newItem
    }

}
