package com.kradic.carpediem.editArticle

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kradic.carpediem.database.Drink
import com.kradic.carpediem.database.DrinkDao
import kotlinx.coroutines.*

class EditArticleViewModel(
    private val drinkKey: Long = 0L,
    dataSource: DrinkDao
) : ViewModel() {

    //Interface za komunikaciju s bazom
    val database = dataSource

    //Varijable za korištenje korutine
    private val job = Job()
    private val editScope = CoroutineScope(Dispatchers.Main + job)

    //MutableLiveData varijable koje se promatraju iz fragmenta
    private val _navigateBack = MutableLiveData<Boolean>()
    val navigateBack: LiveData<Boolean>
        get() = _navigateBack

    private val _update = MutableLiveData<Boolean>()
    val update: LiveData<Boolean>
        get() = _update

    //LiveData pića koje se dohvaća iz baze po id-ju koji je proslijeđen iz ArticlesFragmenta
    var drink: LiveData<Drink> = database.getDrinkById(drinkKey)

    //Varijabla za kategoriju
    var category: String = ""

    //MutableLiveData varijabla za cijenu
    var price = MutableLiveData<String>()


    //Funkcija koja pokreće korutinu kad korisnik klikne POTVRDI
    fun onConfirm(){
        editScope.launch {
            withContext(Dispatchers.IO){
                //Ako kategorija nije prazna, postavi kategoriju piću
                if (category.isNotEmpty()){
                    drink.value?.category = category
                }
                //U suprotnom ostavi kako je bilo
                else{
                    category = drink.value?.category.toString()
                }
                //Ako cijena nije null, postavi novu cijenu
                if (price.value != null){
                    drink.value?.price = price.value!!.toDouble()
                }
                //Updateaj piće u bazi podataka
                database.update(drink.value!!)
            }
        }

        //Postavi update na true
        _update.value = true
    }

    //Funkcije za pokretanje i zaustavljanje navigacije
    fun startNavigating(){
        _navigateBack.value = true
    }

    fun stopNavigating(){
        _navigateBack.value = false
    }

    fun stopUpdating(){
        _update.value = false
    }

}