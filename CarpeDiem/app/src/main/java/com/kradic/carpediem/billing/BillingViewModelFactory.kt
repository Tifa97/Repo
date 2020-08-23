package com.kradic.carpediem.billing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.kradic.carpediem.database.BillDao
import java.lang.IllegalArgumentException

class BillingViewModelFactory(private val isAdmin: Boolean, private val name: String, private val dataSource: BillDao) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(BillingViewModel::class.java)){
            return BillingViewModel(isAdmin, name, dataSource) as T
        }

        throw IllegalArgumentException ("Unknown ViewModel class")
    }

}