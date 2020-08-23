package com.kradic.carpediem.printIncome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kradic.carpediem.database.BillDao
import com.kradic.carpediem.database.SoldDrinkDao
import java.lang.IllegalArgumentException

class PrintIncomeViewModelFactory(
    private val total: String,
    private val drinkDataSource: SoldDrinkDao,
    private val billDataSource: BillDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PrintIncomeViewModel::class.java)){
            return PrintIncomeViewModel(total, drinkDataSource, billDataSource) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}