package com.kradic.carpediem.login

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kradic.carpediem.database.UserDao
import java.lang.IllegalArgumentException

class LoginViewModelFactory(
    private val dataSource: UserDao,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)){
            return LoginViewModel(
                dataSource,
                application
            ) as T
        }
        throw IllegalArgumentException ("Unknown ViewModel class")
    }
}