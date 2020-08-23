package com.kradic.carpediem.bills

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.kradic.carpediem.database.Bill
import com.kradic.carpediem.database.BillDao

class BillsViewModel(
    val dataSource: BillDao,
    application: Application
) : AndroidViewModel(application){

    //LiveData lista računa
    var bills: LiveData<List<Bill>> = dataSource.getAllBills()

    //Ukupan iznos računa
    var billsTotal: Double = 0.00

    //Funkcija koja računa dnevni promet
    fun calculateTotalDailyIncome(){
        bills.value?.forEach {
            billsTotal += it.amount
        }
    }

}