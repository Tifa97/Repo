package com.kradic.carpediem.billing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kradic.carpediem.databinding.ListItemBillDrinkBinding
import com.kradic.carpediem.drinks.ChosenDrink
import com.kradic.carpediem.drinks.ChosenDrinkDiffCallBack

//Adapter za RecyclerView koji prikazuje pića na računu
class BillingAdapter : androidx.recyclerview.widget.ListAdapter<ChosenDrink, BillingAdapter.ViewHolder>(ChosenDrinkDiffCallBack()){

    override fun onBindViewHolder(holder: BillingAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingAdapter.ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ListItemBillDrinkBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChosenDrink){
            binding.billDrink = item
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup): ViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemBillDrinkBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}