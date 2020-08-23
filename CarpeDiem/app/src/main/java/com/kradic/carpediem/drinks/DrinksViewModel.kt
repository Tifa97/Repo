package com.kradic.carpediem.drinks

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kradic.carpediem.MainActivity
import com.kradic.carpediem.database.Drink
import com.kradic.carpediem.database.DrinkDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.sql.DataSource

class DrinksViewModel(dataSource: DrinkDao, category: String) : ViewModel() {

    //Interface za komunikaciju s bazom pića
    val database = dataSource

    //LiveData lista pića po kategoriji iz baze podataka
    var drinks: LiveData<List<Drink>> = database.getDrinksByCategory(category)

    //Prazna lista u koju će se unositi odabrana pića
    val chosenDrinks: ArrayList<ChosenDrink> = ArrayList()

    //MutableLiveData varijable koje se promatraju u fragmentu
    private val _clickedDrinkId = MutableLiveData<Long>()
    val clickedDrinkId: LiveData<Long>
        get() = _clickedDrinkId

    private var clickedDrink: Drink? = null

    private val _drinkCategory = MutableLiveData<String>()
    val drinkCategory: LiveData<String>
        get() = _drinkCategory

    private val _clickedAdd = MutableLiveData<Boolean>()
    val clickedAdd: LiveData<Boolean>
        get() = _clickedAdd

    private val _clickedRemove = MutableLiveData<Boolean>()
    val clickedRemove: LiveData<Boolean>
        get() = _clickedRemove

    private val _clickedConfirm = MutableLiveData<Boolean>()
    val clickedConfirm: LiveData<Boolean>
        get() = _clickedConfirm

    //Varijabla za provjeru postojanja pića
    var exists: Boolean = false

    //Funkcija za dohvaćanje kliknutog pića
    fun getClickedDrink() = clickedDrink

    //Inicijalizacija kategorije pića prema argumentu iz BillingFragmenta
    init {
        _drinkCategory.value = category
    }

    //Funkcija koja postavlja id kliknutog pića
    fun onDrinkClicked(drinkId: Long){
        _clickedDrinkId.value = drinkId
    }

    //Funkcije za pokretanje i zaustavljanje dodavanja, uklanjanja i potvrde
    fun onAddClicked(){
        _clickedAdd.value = true
    }

    fun endAdding(){
        _clickedAdd.value = false
    }

    fun onRemoveClicked(){
        _clickedRemove.value = true
    }

    fun endRemoving(){
        _clickedRemove.value = false
    }

    fun onConfirmedClicked(){
        _clickedConfirm.value = true
    }

    fun onConfirmClickDone(){
        _clickedConfirm.value = false
    }

    //Funkcija za postavljanje kliknutog pića
    fun setClickedDrink(drinkId: Long){
        //Za svako piće u LiveData listi pića
        for (drink in drinks.value!!) {
            //Provjeri postoji li piće s jednakim id-jem
            if (drink.drinkId == drinkId){
                //To piće postavi kao kliknuto
                clickedDrink = drink
            }
        }
    }

    //Funkcija za dodavanje odabranog pića u listu
    fun addChosenDrinkToList(){
        //Postavi kliknuto piće kao tempDrink
        var tempDrink = ChosenDrink(clickedDrink?.drink.toString(), clickedDrink?.price!!)

        //Za sva pića u listi odabranih pića
        for (drink in chosenDrinks) {
            //Provjeri postoji li već piće s jednakim imenom kao tempDrink
            if (tempDrink.drinkName == drink.drinkName){
                exists = true
            }
        }
        //Ako ne postoji, dodaj tempDrink u listu odabranih pića
        if (exists == false){
            chosenDrinks.add(tempDrink)
        }
        //Ako postoji
        else{
            //Prođi kroz sva pića u listi odabranih pića
            for (drink in chosenDrinks) {
                //Ako postoji piće jednako tempDrinku, updateaj mu cijenu i broj koliko je puta odabrano
                if (tempDrink.drinkName == drink.drinkName){
                    drink.price += tempDrink.price
                    drink.noOfChosen++
                }
            }
        }

        //Vrati exists na false
        exists = false
    }

    //Funckija za uklanjanje pića s liste odabranih pića
    fun removeChosenDrinkFromList(){
        //Postavi kliknuto piće kao tempDrink
        var tempDrink = ChosenDrink(clickedDrink?.drink.toString(), clickedDrink?.price!!)

        //Za sva pića u listi odabranih pića
        for (drink in chosenDrinks) {
            //Provjeri postoji li već piće s jednakim imenom kao tempDrink
            if (tempDrink.drinkName == drink.drinkName){
                exists = true
            }
        }

        //Ako postoji
        if (exists == true){
            //Prođi kroz sva pića u listi odabranih pića
            for(drink in chosenDrinks){
                //Ako je piće odabrano više od jednog puta, smanji cijenu i broj puta koje je odabrano za 1
                if(tempDrink.drinkName == drink.drinkName && drink.noOfChosen > 1){
                    drink.price -= tempDrink.price
                    drink.noOfChosen--
                }
                //Ako je odabrano jednom, ukloni ga s liste odabranih pića
                else if (tempDrink.drinkName == drink.drinkName && drink.noOfChosen <= 1){
                    chosenDrinks.remove(drink)
                    break
                }
            }
        }

        //Postavi exists na false
        exists = false
    }
}

