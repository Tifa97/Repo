package com.kradic.carpediem.editArticle

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kradic.carpediem.database.DrinkDao
import java.lang.IllegalArgumentException
import javax.sql.DataSource

class EditArticleViewModelFactory(
    private val drinkId: Long,
    private val dataSource: DrinkDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(EditArticleViewModel::class.java)){
            return EditArticleViewModel(drinkId, dataSource) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}