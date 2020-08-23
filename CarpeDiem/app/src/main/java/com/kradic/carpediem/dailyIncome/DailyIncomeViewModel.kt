package com.kradic.carpediem.dailyIncome

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kradic.carpediem.database.Bill
import com.kradic.carpediem.database.BillDao
import com.kradic.carpediem.database.SoldDrink
import com.kradic.carpediem.database.SoldDrinkDao
import kotlinx.coroutines.*
import javax.sql.DataSource

class DailyIncomeViewModel(
    private val drinksDataSource: SoldDrinkDao,
    private val billsDataSource: BillDao,
    application: Application
) : AndroidViewModel(application){

    //LiveData liste prodanih pića i računa
    var soldDrinks: LiveData<List<SoldDrink>> = drinksDataSource.getAllSoldDrinks()

    var bills: LiveData<List<Bill>> = billsDataSource.getAllBills()

    //Varijabla za ukupan dnevni promet
    var totalDailyIncome: Double = 0.00

    //MutableLiveData varijabla koja promatra je li korisnik kliknuo ISPIŠI
    private val _navigateToPrint = MutableLiveData<Boolean?>()
    val navigateToPrint: LiveData<Boolean?>
        get() = _navigateToPrint

    //Funkcija koja računa dnevni promet
    fun calculateTotalDailyIncome(){
        bills.value?.forEach {
            totalDailyIncome += it.amount
        }
    }

    //Funkcije za pokretanje i zaustavljanje navigacije prema PrintIncome fragmentu
    fun navigateToPrint(){
        _navigateToPrint.value = true
    }

    fun stopNavigatingToPrint(){
        _navigateToPrint.value = false
    }
}