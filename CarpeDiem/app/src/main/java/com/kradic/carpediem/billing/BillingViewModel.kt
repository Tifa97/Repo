package com.kradic.carpediem.billing


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kradic.carpediem.*
import com.kradic.carpediem.database.Bill
import com.kradic.carpediem.database.BillDao
import com.kradic.carpediem.drinks.ChosenDrink
import kotlinx.android.synthetic.main.fragment_billing.*
import kotlinx.coroutines.*

class BillingViewModel(isAdmin: Boolean, name: String, dataSource: BillDao) : ViewModel() {

    //Inicijalizacija interfacea za komunikaciju s bazom računa
    private val billDao = dataSource

    //Varijable za korištenje korutine
    private var viewModelJob = Job()
    private val scope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //MutableLiveData varijable koje se promatraju iz fragmenta
    private val _adminStatus = MutableLiveData<Boolean>()
    val adminStatus: LiveData<Boolean>
        get() = _adminStatus

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String>
        get() = _userName

    private val _navigateBackHome = MutableLiveData<Boolean?>()
    val navigateBackHome: LiveData<Boolean?>
        get() = _navigateBackHome

    private val _navigateToDrinks = MutableLiveData<Boolean?>()
    val navigateToDrinks: LiveData<Boolean?>
        get() = _navigateToDrinks

    //Varijabla za kategoriju kojom se određuje koja kategorija će se prikazati klikom gumba
    var category: String = ""

    //MutableLiveData varijabla koja promatra broj računa
    private var _numberOfBills = MutableLiveData<Long>()
    var numberOfBills: LiveData<Long> = _numberOfBills

    //Inicijalizacija broja računa, usernamea i admin statusa
    init{
        initializeNoOfBills()
        _userName.value = name
        _adminStatus.value = isAdmin
    }

    //Pokretanje korutine za dobivanje broja računa
    private fun initializeNoOfBills() {
        scope.launch {
            _numberOfBills.value = getNoOfBills()
        }
    }

    //Suspendirana funkcija koja dobiva broj računa iz baze podataka
    private suspend fun getNoOfBills(): Long? {
        return withContext(Dispatchers.IO){
            billDao.getNoOfBills()
        }
    }

    //Funkcije za pokretanje i zaustavljanje navigacije na glavni ekran
    fun startNavigatingBackHome(){
        _navigateBackHome.value = true
    }

    fun stopNavigatingBackHome(){
        _navigateBackHome.value = false
    }

    //Funkcije za pokretanje i zaustavljanje navigacije na ekrane s pićima s nazivom kategorije
    fun startNavigatingToDrinksFromJuices(){
        category = getSokoviCat()
        _navigateToDrinks.value = true
    }

    fun startNavigatingToDrinksFromWater(){
        category = getVodaCat()
        _navigateToDrinks.value = true
    }

    fun startNavigatingToDrinksFromBeer(){
        category= getPivaCat()
        _navigateToDrinks.value = true
    }

    fun startNavigatingToDrinksFromWhiskey(){
        category= getWhiskeyCat()
        _navigateToDrinks.value = true
    }

    fun startNavigatingToDrinksFromJegger(){
        category= getJeggerCat()
        _navigateToDrinks.value = true
    }

    fun startNavigatingToDrinksFromGin(){
        category= getGinCat()
        _navigateToDrinks.value = true
    }

    fun startNavigatingToDrinksFromVodka(){
        category= getVodkaCat()
        _navigateToDrinks.value = true
    }

    fun startNavigatingToDrinksFromTekila(){
        category= getTekilaCat()
        _navigateToDrinks.value = true
    }

    fun startNavigatingToDrinksFromRest(){
        category= getRestCat()
        _navigateToDrinks.value = true
    }

    fun stopNavigatingToDrinks(){
        _navigateToDrinks.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}