package com.kradic.carpediem

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.kradic.carpediem.database.*
import com.kradic.carpediem.drinks.ChosenDrink
import com.kradic.carpediem.printIncome.PrintIncomeFragment
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.utilities.Printing
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {

    //Job i Scope za komunikaciju s bazom podataka
    private val activityJob = Job()
    private val activityScope = CoroutineScope(Dispatchers.Main + activityJob)

    //Za provjeru vremena od posljednjeg klika back gumba
    private var backPressedTime: Long = 0
    //Za ispis poruke pri kliku back gumba
    private var backToast: Toast? = null

    //Za postavljanje popusta i preračunavanja u eure
    internal var enableDiscount: Boolean = false
    internal var enableEuro: Boolean = false

    //Vrijednost eura u odnosu na kunu
    var euro: Double = 7.40

    //Lista odabranih pića iz DrinksFragmenta koja se koristi za prikaz odabranih pića na računu
    var chosenDrinksList: ArrayList<ChosenDrink> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Inicijalizacija Printootha
        Printooth.init(this)

        val application = requireNotNull(this).application

        //Inicijalizacija interfacea za komunikaciju s bazom
        val drinkDao = CarpeDiemDatabase.getInstance(application).drinkDao
        val userDao = CarpeDiemDatabase.getInstance(application).userDao

        //Lista svih pića u bazi podataka
        val drinkList: LiveData<List<Drink>> = drinkDao.getAllDrinks()

        //Prazna lista pića koju ćemo napuniti i zatim unijeti u bazu
        val drinks: ArrayList<Drink> = ArrayList()

        //Lista svih korisnika u bazi podataka
        val userList: LiveData<List<User>> = userDao.getAllUsersLive()

        //Prazna lista korisnika koju punimo i zatim unosimo z bazu
        val users: ArrayList<User> = ArrayList()


        //Stvaranje 4 nova korisnika
        val admin = User()

        admin.name = "Admin"
        admin.password = "boss2020"
        admin.isAdmin = true

        val user1 = User()

        user1.name = "Konobar1"
        user1.password = "konobar"
        user1.isAdmin = false


        val user2 = User()

        user2.name = "Konobar2"
        user2.password = "kelner"
        user2.isAdmin = false

        val user3 = User()
        user3.name = "Konobar3"
        user3.password = "posluzitelj"
        user3.isAdmin = false

        //Unos korisnika u listu
        users.add(admin)
        users.add(user1)
        users.add(user2)
        users.add(user3)

        //Stvaranje novih pića
        val cola = Drink()

        cola.category = getSokoviCat()
        cola.drink = "Coca cola"
        cola.price = 10.00

        val sprite = Drink()

        sprite.category = getSokoviCat()
        sprite.drink = "Sprite"
        sprite.price = 10.00

        val voda = Drink()

        voda.category = getVodaCat()
        voda.drink = "voda"
        voda.price = 10.00

        val jack = Drink()

        jack.category = getWhiskeyCat()
        jack.drink = "Jack Daniels"
        jack.price = 20.00

        val smugller = Drink()

        smugller.category = getWhiskeyCat()
        smugller.drink = "Smuggler"
        smugller.price = 15.00

        val smirnoff = Drink()

        smirnoff.category = getVodkaCat()
        smirnoff.drink = "Smirnoff"
        smirnoff.price = 10.00

        val ozujsko = Drink()

        ozujsko.category = getPivaCat()
        ozujsko.drink = "Ožujsko"
        ozujsko.price = 10.00

        val jeggerMeister = Drink()

        jeggerMeister.category = getJeggerCat()
        jeggerMeister.drink = "Jeggermeister"
        jeggerMeister.price = 10.00


        val gordons = Drink()

        gordons.category = getGinCat()
        gordons.drink = "Gordon's"
        gordons.price = 10.00

        val sierra = Drink()

        sierra.category = getTekilaCat()
        sierra.drink = "Sierra"
        sierra.price = 10.00

        val espresso = Drink()

        espresso.category = getRestCat()
        espresso.drink = "Espresso"
        espresso.price = 10.00

        //Unos pića u listu
        drinks.add(cola)
        drinks.add(sprite)
        drinks.add(voda)
        drinks.add(jack)
        drinks.add(smugller)
        drinks.add(smirnoff)
        drinks.add(ozujsko)
        drinks.add(jeggerMeister)
        drinks.add(gordons)
        drinks.add(sierra)
        drinks.add(espresso)

        //Promatraj listu pića u bazi, ukoliko je prazna, napuni je gore stvorenim pićima
        drinkList.observe(this, Observer {
            if (it.isEmpty()){
                populateDrinks(drinkDao, drinks)
            }
        })

        //Promatraj listu korisnika u bazi, ukoliko je prazna, napuni je gore stvorenim korisnicima
        userList.observe(this, Observer {
            if (it.isEmpty()){
                populateUsers(userDao, users)
            }
        })

    }

    //Override pritiska gumba back
    override fun onBackPressed() {
        //Dohvati id trenutno otvorenog fragmenta
        val currentFragmentId = NavHostFragment.findNavController(nav_host_fragment).currentDestination!!.id

        when(currentFragmentId){
            //Ako je otvoren HomeFragment, onemogući gumb back
            //Povratak na ekran za prijavu moguć samo odjavom
            R.id.homeFragment -> {
                backToast = Toast.makeText(getBaseContext(), "Odjavite se za povratak na ekran za prijavu", Toast.LENGTH_SHORT)
                backToast?.show()
                return
            }
            //Ako je otvoren LoginFragment, omogući izlaz iz aplikacije duplim klikom na gumb back
            R.id.loginFragment ->{
                if (backPressedTime + 2000 > System.currentTimeMillis()){
                    backToast?.cancel()
                    this.finishAffinity()
                    exitProcess(0)
                    return
                } else{
                    backToast = Toast.makeText(getBaseContext(), "Ponovno pritisnite natrag za gašenje aplikacije", Toast.LENGTH_SHORT)
                    backToast?.show()
                }
                backPressedTime = System.currentTimeMillis()
            }
            //Onemogući povratak na prethodni ekran putem gumba back s PrintIncomeFragmenta
            R.id.printIncomeFragment -> {
                backToast = Toast.makeText(getBaseContext(), "Vratite se na prethodni ekran putem gumba POVRATAK", Toast.LENGTH_SHORT)
                backToast?.show()
                return
            }
            else -> super.onBackPressed()
        }
    }

    //Funkcija za popunjavanje baze gore stvorenim pićima
    private fun populateDrinks(dataSource: DrinkDao, drinks: ArrayList<Drink>) {
        activityScope.launch(Dispatchers.IO) {
            for (drink in drinks) {
                dataSource.insert(drink)
            }
        }
    }

    //Funkcija koja dohvaća listu odabranih pića
    internal fun getListOfChosenDrinks(): ArrayList<ChosenDrink>{
        return chosenDrinksList
    }

    //Funkcija za popunjavanje baze gore stvorenim korisnicima
    private fun populateUsers(userDao: UserDao, users: ArrayList<User>) {
        activityScope.launch (Dispatchers.IO){
            for (user in users) {
                    userDao.insert(user)
            }
        }
    }

}
