package com.kradic.carpediem.addArticle

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kradic.carpediem.database.Drink
import com.kradic.carpediem.database.DrinkDao
import kotlinx.coroutines.*
import java.math.BigInteger

class AddArticleViewModel(private val dataSource: DrinkDao): ViewModel() {
    //Interface za komunikaciju s bazom
    val database = dataSource

    //Varijable za korištenje korutina
    private val job = Job()
    private val editScope = CoroutineScope(Dispatchers.Main + job)

    //Lista u koju će se spremati pića
    private var drinks: List<Drink> = emptyList()

    //MutableLiveData varijabla koja provjerava želi li korisnik potvrditi unos
    private val _navigateBack = MutableLiveData<Boolean?>()
    val navigateBack: LiveData<Boolean?>
        get() = _navigateBack

    //MutableLiveData varijable za naziv i cijenu artikla
    var article = MutableLiveData<String>()

    var price = MutableLiveData<String>()

    //Varijabla za kategoriju artikla
    var category: String = ""

    //Varijabla pomoću koje se provjerava postojanje artikla
    var exists: Boolean = false

    //Funkcija koja pokreće korutinu za učitavanje svih pića
    fun loadDrinks(){
        editScope.launch {
            drinks = getAllDrinks()
        }
    }

    //Suspendirana funkcija koja dohvaća sva pića iz baze
    private suspend fun getAllDrinks(): List<Drink> {
        return withContext(Dispatchers.IO){
            database.getAllDrinksList()
        }
    }

    //Funkcija za pokretanje korutine za unos pića u bazu
    fun insertDrink(){
        editScope.launch {
            //Stvori novo prazno piće
            var newDrink = Drink()

            //Ako naziv i cijena pića nisu null
            if (article.value != null && price.value != null){
                //Postavi vrijednosti
                newDrink.drink = article.value!!
                newDrink.category = category
                newDrink.price = price.value!!.toDouble()

                //Provjeri postoji li piće već u bazi
                checkIfDrinkExists()

                //Ako piće ne postoji, unesi ga u bazu
                if (exists == false){
                    insert(newDrink)
                }

                //Započni povratak na prethodni ekran
                _navigateBack.value = true
            }
        }
    }

    //Funkcija za dovršavanje povratka na prethodni ekran
    fun stopNavigatingBack(){
        _navigateBack.value = false
    }

    //Suspendirana funkcija za unos pića u bazu
    private suspend fun insert(newDrink: Drink) {
        withContext(Dispatchers.IO){
            database.insert(newDrink)
        }
    }

    //Funkcija koja provjerava postoji li piće
    private fun checkIfDrinkExists() : Boolean{
        exists = false

        drinks.forEach {
            if (it.drink == article.value){
                exists = true
            }
        }

        return exists
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}