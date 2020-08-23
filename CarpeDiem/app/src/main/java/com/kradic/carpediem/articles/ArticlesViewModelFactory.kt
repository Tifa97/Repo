package com.kradic.carpediem.articles

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kradic.carpediem.database.DrinkDao
import java.lang.IllegalArgumentException

class ArticlesViewModelFactory(
    private val dataSource: DrinkDao,
    private val application: Application
) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ArticlesViewModel::class.java)){
            return ArticlesViewModel(dataSource, application) as T
        }

        throw IllegalArgumentException ("Unknown ViewModel class")
    }
}