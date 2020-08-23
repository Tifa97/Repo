package com.kradic.carpediem.drinks

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kradic.carpediem.database.DrinkDao
import java.lang.IllegalArgumentException
import javax.sql.DataSource

class DrinksViewModelFactory(
    private val dataSource: DrinkDao,
    private val category: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(DrinksViewModel::class.java)){
            return DrinksViewModel(dataSource, category) as T
        }
        throw IllegalArgumentException ("Unknown ViewModel class")
    }
}