package com.kradic.carpediem.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class HomeViewModelFactory(private val userName: String,
                           private val isAdmin: Boolean) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)){
            return HomeViewModel(userName, isAdmin) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}