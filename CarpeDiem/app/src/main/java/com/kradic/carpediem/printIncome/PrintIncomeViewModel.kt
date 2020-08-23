package com.kradic.carpediem.printIncome

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

import com.kradic.carpediem.database.BillDao
import com.kradic.carpediem.database.SoldDrink
import com.kradic.carpediem.database.SoldDrinkDao
import kotlinx.coroutines.*

class PrintIncomeViewModel(
    total: String,
    drinkDataSource: SoldDrinkDao,
    billDataSource: BillDao
) : ViewModel(){

    //Interface za komunikaciju s bazom
    private val soldDrinksdao = drinkDataSource
    private val billDao = billDataSource

    //Varijable za korištenje korutina
    private val job = Job()
    private val editScope = CoroutineScope(Dispatchers.Main + job)

    //LiveData lista prodanih piča
    var soldDrinks: LiveData<List<SoldDrink>> = soldDrinksdao.getAllSoldDrinks()

    //Dnevni promet proslijeđen iz DailyIncomeFragmenta
    val income = total

    //Funkcija za korutinu koja briše prodana pića i račune iz baze podataka
    fun clearBillsAndSoldDrinks(){
        editScope.launch(Dispatchers.IO) {
            soldDrinksdao.clear()
            billDao.clear()
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}