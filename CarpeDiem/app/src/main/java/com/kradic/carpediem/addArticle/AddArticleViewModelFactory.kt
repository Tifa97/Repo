package com.kradic.carpediem.addArticle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kradic.carpediem.database.DrinkDao
import java.lang.IllegalArgumentException

class AddArticleViewModelFactory(private val dataSource: DrinkDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AddArticleViewModel::class.java)){
            return AddArticleViewModel(dataSource) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}