package com.kradic.carpediem.dailyIncome

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kradic.carpediem.database.BillDao
import com.kradic.carpediem.database.SoldDrinkDao
import java.lang.IllegalArgumentException

class DailyIncomeViewModelFactory (
    private val drinksDataSource: SoldDrinkDao,
    private val billsDataSource: BillDao,
    private val application: Application
) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DailyIncomeViewModel::class.java)){
            return DailyIncomeViewModel(drinksDataSource, billsDataSource, application) as T
        }

        throw IllegalArgumentException ("Unknown ViewModelClass")
    }
}