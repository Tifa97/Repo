package com.kradic.carpediem.articles

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.kradic.carpediem.*
import com.kradic.carpediem.database.Drink
import com.kradic.carpediem.database.DrinkDao
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException

class ArticlesViewModel(
    dataSource: DrinkDao, application: Application
) : AndroidViewModel(application) {

    //Interface s bazom
    val database = dataSource

    //LiveData lista pića iz baze podataka
    var drinks: LiveData<List<Drink>> = database.getAllDrinks()

    //Varijable potrebne za korištenje korutine
    private val viewModelJob = Job()
    private val articlesScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //MutableLiveData varijable koje se promatraju iz fragmenta
    private val _clickedDrinkId = MutableLiveData<Long>()
    val clickedDrinkId: LiveData<Long>
        get() = _clickedDrinkId

    private val _clickedEdit = MutableLiveData<Boolean?>()
    val clickedEdit: LiveData<Boolean?>
        get() = _clickedEdit

    private val _clickedAdd = MutableLiveData<Boolean?>()
    val clickedAdd: LiveData<Boolean?>
        get() = _clickedAdd

    //Varijabla u koju će se spremati kliknuto piće
    private var clickedDrink: Drink? = null

    //Funkcija za dohvaćanje kliknutog pića
    fun getClickedDrink() = clickedDrink

    //Funkcija koja postavlja id kliknutog pića
    fun onDrinkClicked(drinkId: Long){
        _clickedDrinkId.value = drinkId
    }

    //Funkcija koja postavlja kliknuto piće
    fun setClickedDrink(drinkId: Long){
        //Za svako piće u LiveData listi pića
        for (drink in drinks.value!!) {
            //Ako postoji piće s tim id-jem, postavi to piće kao kliknuto
            if (drink.drinkId == drinkId){
                clickedDrink = drink
            }
        }
    }

    //Funkcija za brisanje kliknutog pića
    fun removeClickedDrink(){
        //Ako je kliknuto neko piće, pokreni korutinu iz koje ćeš izbrisati piće iz baze po id-ju
        if (clickedDrink != null){
            articlesScope.launch {
                withContext(Dispatchers.IO){
                    database.deleteDrinkWithId(clickedDrink!!.drinkId)
                }
            }
            _clickedDrinkId.value = null
        }
    }

    //Funkcije za pokretanje i zaustavljanje navigacije
    fun startNavigatingToEdit(){
        _clickedEdit.value = true
    }

    fun stopNavigatingToEdit(){
        _clickedEdit.value = false
    }

    fun startNavigatingToAdd(){
        _clickedAdd.value = true
    }

    fun stopNavigatingToAdd(){
        _clickedAdd.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}