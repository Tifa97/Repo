package com.kradic.carpediem.billing

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.kradic.carpediem.MainActivity
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import com.kradic.carpediem.R
import com.kradic.carpediem.database.*
import com.kradic.carpediem.databinding.FragmentBillingBinding
import com.kradic.carpediem.drinks.ChosenDrink
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.RawPrintable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class BillingFragment : Fragment() {

    //Privatne varijable s vrijednostima
    private var discount: Double = 30.00
    private var inEuro: Double = 0.00
    private var PDV: Double = 1.25
    private var osnovica: Double = 0.00
    private var PDVIznos: Double = 0.00

    //Varijabla za id računa
    private var billNo: Long = 0

    //Varijable za provjeru dozvole popusta i preračunavanja u eure
    private var isDiscounted: Boolean = false
    private var isTurnedToEur: Boolean = false

    //Printing potreban za korištenje Printootha
    private var printing : Printing? = null

    //Prazna instanca računa koja se puni prije ispisa računa i unosi u bazu nakon
    private var bill: Bill = Bill()

    //Varijabla za listu odabranih pića na računu
    lateinit var drinksOnBill: ArrayList<ChosenDrink>

    //Lista u koju će se ubacivati prodana pića, a zatim unositi u bazu
    var soldDrinks = emptyList<SoldDrink>()

    //Varijable za korištenje korutina
    val job = Job()
    val billingScope = CoroutineScope(Dispatchers.Main + job)

    //MutableLiveData koji prati je li printanje bilo uspješno
    private val _success = MutableLiveData<Boolean?>()
    val success: LiveData<Boolean?>
        get() = _success

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentBillingBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_billing, container, false)

        //Inicijalizacija aplikacije i interfacea za komunikaciju s bazom
        val application = requireNotNull(activity as MainActivity).application
        val billDao: BillDao = CarpeDiemDatabase.getInstance(application).billDao
        val soldDrinkDao: SoldDrinkDao = CarpeDiemDatabase.getInstance(application).soldDrinkDao

        //Postavi vrijednost eura prema vrijednosti u MainActivityju
        var eur = (activity as MainActivity).euro

        //Označi da iznos računa nije pretvoren u eure niti je uračunat popust
        isTurnedToEur = false
        isDiscounted = false

        //Učitaj listu prodanih pića
        loadSoldDrinks(soldDrinkDao)

        //Inicijalizacija factoryja i ViewModela
        val billingViewModelFactory
                = BillingViewModelFactory(BillingFragmentArgs.fromBundle(arguments!!).isAdmin, BillingFragmentArgs.fromBundle(arguments!!).userName, billDao)

        val billingViewModel
                = ViewModelProviders.of(this, billingViewModelFactory).get(BillingViewModel::class.java)

        binding.billingViewModel = billingViewModel

        //Dohvaćanje liste pića na računu iz MainActivityja
        drinksOnBill = (activity as MainActivity).getListOfChosenDrinks()

        //Provjera uparenosti s printerom, potrebno za korištenje Printootha
        if (Printooth.hasPairedPrinter()){
            printing = Printooth.printer()
        }

        //Poziv funkcije koja inicijalizira Printooth listenere
        initListeners()

        //Prikaži gumb za popust ovisno o tome da li je enablean ili nije
        if((activity as MainActivity).enableDiscount == true){
            binding.btnPopust.visibility = View.VISIBLE
        }
        else{
            binding.btnPopust.visibility = View.GONE
        }

        //Prikaži gumb za preračunavanje u eure ovisno o tome da li je enablean ili nije
        if ((activity as MainActivity).enableEuro == true){
            binding.btnEuro.visibility = View.VISIBLE
        }
        else{
            binding.btnEuro.visibility = View.GONE
        }

        //Postavi ukupni iznos računa
        var billSum = calculateSum(drinksOnBill)

        //Postavljanje teksta za iznos računa
        binding.txtSum.text = billSum.toString() + " kn"

        //Klikom na gumb Euro, preračunaj u eure i postavi tekst za iznos računa
        binding.btnEuro.setOnClickListener{
            if (isTurnedToEur == false){
                binding.txtTotal.text = "Total u Eurima: "
                inEuro = Math.round(billSum / eur * 100) / 100.00
                binding.txtSum.text = inEuro.toString() + " EUR"
                isTurnedToEur = true
            }
            else{

            }
        }

        //Klikom na gumb Popust, uračunaj popust u cijenu
        binding.btnPopust.setOnClickListener{
            if (isDiscounted == false){
                binding.txtTotal.text = "Uz ${discount.toInt()}% popusta: "
                billSum -= (billSum * discount/100.00)
                binding.txtSum.text = billSum.toString() + " kn"
                isDiscounted = true
            }
            else{

            }
        }

        //postavi adapter za RecyclerView koji ispisuje pića na računu
        val billingAdapter = BillingAdapter()

        binding.billDrinks.adapter = billingAdapter
        billingAdapter.submitList(drinksOnBill)

        //Klikom na gumb POVRATAK, vrati na HomeFragment i postavi _navigateBackHome na false
        billingViewModel.navigateBackHome.observe(this, Observer {
            if (it == true){
                requireActivity().onBackPressed()
                billingViewModel.stopNavigatingBackHome()
            }
        })

        //Klikom na gumb POVRATAK, otiđi na DirnksFragment i postavi _navigateToDrinks na false
        billingViewModel.navigateToDrinks.observe(this, Observer {
            if (it == true){
                this.findNavController().navigate(BillingFragmentDirections.actionBillingFragmentToDrinksFragment(
                    billingViewModel.category))
                billingViewModel.stopNavigatingToDrinks()
            }
        })

        //Promatraj broj računa i postavi u varijablu billNo
        billingViewModel.numberOfBills.observe(this, Observer {
            it?.let {
                billNo = it
            }
        })

        //Klikom na gumb PONIŠTI, vrati fragment na početno stanje
        binding.btnDeleteAll.setOnClickListener {
            drinksOnBill.clear()
            binding.billDrinks.adapter = billingAdapter
            billingAdapter.submitList(drinksOnBill)
            binding.txtSum.text = calculateSum(drinksOnBill).toString()
        }

        //Klik na gumb Ispiši
        binding.btnWrite.setOnClickListener {
            //Provjeri je li iznos računa 0
            if(billSum != 0.00){
                //Ako nije, provjeri postoji li upareni printer, ako nije, pokreni Activity za uparivanje
                if(!Printooth.hasPairedPrinter()){
                    startActivityForResult(Intent(context, ScanningActivity::class.java), ScanningActivity.SCANNING_FOR_PRINTER)
                }
                //Ako postoji upareni printer
                else{
                    //Stvori novi račun
                    createBill(billSum)
                    //Poziv funkcije za izračun poreza
                    calculateTaxes(billSum)
                    //Isprintaj račun
                    printPrintables(bill)
                    //Promatraj da li je printanje uspješno
                    success.observe(this, Observer {
                        //Ako je uspješno, unesi račun i pića u bazu podataka i postavi fragment na početno stanje
                        if (it == true){
                            insertBillIntoDatabase(bill, billDao)
                            insertDrinksIntoDatabase(drinksOnBill, soldDrinkDao)
                            drinksOnBill.clear()
                            binding.billDrinks.adapter = billingAdapter
                            billingAdapter.submitList(drinksOnBill)
                            binding.txtSum.text = calculateSum(drinksOnBill).toString()
                        }
                    })
                }
            }
            //Ako je iznos računa 0, ispiši poruku
            else{
                Toast.makeText(context, "Ne možete isprintati prazan račun", Toast.LENGTH_SHORT).show()
            }
        }

        //Manager za prikaz RecyclerViewa
        val manager = GridLayoutManager(activity, 1)
        binding.billDrinks.layoutManager = manager



        return binding.root
    }

    //Funkcija za izračun osnovice i iznosa PDV-a
    private fun calculateTaxes(billSum: Double) {
        osnovica = billSum/PDV
        PDVIznos = billSum - osnovica
    }

    //Funkcija za stvaranje novog računa
    private fun createBill(billSum: Double) {
        val sdf = SimpleDateFormat("HH:mm:ss").format(Date())

        bill.amount = billSum
        bill.billNumber = getRandomString(30)
        bill.time = sdf
    }

    //Funkcija za ispis računa
    private fun printPrintables(bill: Bill?) {
        //Dohvati ArrayListu Printablea koji se moraju naći na računu
        val printables: ArrayList<Printable> = getPrintables(bill)
        //Printaj Printablese
        printing?.print(printables)
    }

    //Funkcija koja određuje izgled računa
    private fun getPrintables(bill: Bill?) = ArrayList<Printable>().apply {
        add(RawPrintable.Builder(byteArrayOf(27, 100, 4)).build())

          add(TextPrintable.Builder()
              .setText("Carpe Diem")
              .setFontSize(DefaultPrinter.FONT_SIZE_LARGE)
              .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
              .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
              .setNewLinesAfter(1)
              .build())

          add(TextPrintable.Builder()
              .setText("Ilica 5")
              .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
              .setNewLinesAfter(1)
              .build())

          add(TextPrintable.Builder()
              .setText("Naziv poduzeca d.o.o.")
              .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
              .setNewLinesAfter(1)
              .build())

          add(TextPrintable.Builder()
              .setText("OIB: 12345678975")
              .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
              .setNewLinesAfter(1)
              .build())

        add(
            TextPrintable.Builder()
                .setText("Racun broj: ${billNo + 1}")
                .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                .setNewLinesAfter(1)
                .build()
        )

        add(
            TextPrintable.Builder()
                .setText("Vrijeme izdavanja: ${bill?.time}")
                .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                .setNewLinesAfter(1)
                .build()
        )

        add(
            TextPrintable.Builder()
                .setText("Oznaka operatera: 16")
                .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                .setNewLinesAfter(1)
                .build()
        )

        add(
            TextPrintable.Builder()
                .setText("Placanje: NOVCANICE")
                .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                .setNewLinesAfter(1)
                .build()
        )

        add(
            TextPrintable.Builder()
                .setText("Artikl       komada  cijena \n--------------------------------")
                .setNewLinesAfter(1)
                .build()
        )

        drinksOnBill.forEach {
            add(
                TextPrintable.Builder()
                    .setText("${it.drinkName}                                       ${it.noOfChosen}x  ${it.price} kn \n--------------------------------")
                    .setAlignment(DefaultPrinter.Companion.ALIGNMENT_RIGHT)
                    .setCharacterCode(DefaultPrinter.Companion.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                    .setLineSpacing(DefaultPrinter.LINE_SPACING_30)
                    .build()
            )
        }

        if (isDiscounted == true) {
            add(
                TextPrintable.Builder()
                    .setText("Popust:                     ${discount.toInt()}%")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setAlignment(DefaultPrinter.ALIGNMENT_RIGHT)
                    .setNewLinesAfter(1)
                    .build()
            )
            add(
                TextPrintable.Builder()
                    .setText("Ukupno:                  ${bill?.amount} kn")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setAlignment(DefaultPrinter.ALIGNMENT_RIGHT)
                    .setNewLinesAfter(1)
                    .build()
            )
        } else if (isTurnedToEur == true) {
            add(
                TextPrintable.Builder()
                    .setText("Ukupno:                $inEuro EUR")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setAlignment(DefaultPrinter.ALIGNMENT_RIGHT)
                    .setNewLinesAfter(1)
                    .build()
            )
        } else {
            add(
                TextPrintable.Builder()
                    .setText("Ukupno:                  ${bill?.amount} kn")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setAlignment(DefaultPrinter.ALIGNMENT_RIGHT)
                    .setNewLinesAfter(2)
                    .build()
            )
        }

        add(
            TextPrintable.Builder()
                .setText("Naziv    Stopa  Osnovica  Iznos \n--------------------------------\nPDV:   25,00%  ${"%.2f".format(osnovica)}kn  ${"%.2f".format(PDVIznos)}kn")
                .setNewLinesAfter(2)
                .build()
        )


        add(TextPrintable.Builder()
            .setText("----Zastitni kod izdavatelja----\n ogi2u4r93irdwiu032ur2rifw3k033")
            .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
            .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
            .setNewLinesAfter(1)
            .build()
        )

        add(TextPrintable.Builder()
            .setText("---------JIR--------\n ${bill?.billNumber}")
            .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
            .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
            .setNewLinesAfter(2)
            .build()
        )
    }

    //Funkcija za Printooth listenere
    private fun initListeners() {
        printing?.printingCallback = object : PrintingCallback {
            override fun connectingWithPrinter() {
                Toast.makeText(context, "Connecting with printer", Toast.LENGTH_SHORT).show()
            }

            override fun connectionFailed(error: String) {
                Toast.makeText(context, "Failed to connect printer", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: String) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }

            override fun onMessage(message: String) {
                Toast.makeText(context, "Message: $message", Toast.LENGTH_SHORT).show()
            }

            override fun printingOrderSentSuccessfully() {
                //Postavi _success.value na true ako je uspješno printano
                _success.value = true
                Toast.makeText(context, "Order sent to printer", Toast.LENGTH_SHORT).show()
            }

        }
    }

    //Funkcija koja pokreće korutinu za učitvanje prodanih pića
    private fun loadSoldDrinks(
        soldDrinkDao: SoldDrinkDao
    ){
        billingScope.launch {
            soldDrinks = getSoldDrinks(soldDrinkDao)
        }
    }

    //Suspendirana funkcija (ostatak aplikacije nastavlja s radom dok se ona izvršava) koja učitvara prodana pića iz baze
    private suspend fun getSoldDrinks(soldDrinkDao: SoldDrinkDao): List<SoldDrink> {
        return withContext(Dispatchers.IO){
            soldDrinkDao.getAllSoldDrinksList()
        }
    }

    //Funkcija za unos pića u bazu podataka
    private fun insertDrinksIntoDatabase(
        drinksOnBill: ArrayList<ChosenDrink>,
        soldDrinkDao: SoldDrinkDao
    ) {
        var exists = false

        //Za svako piće na računu
        for (chosenDrink in drinksOnBill) {
            //Prođi po listi prodanih pića
            for (soldDrink in soldDrinks) {
                //Ukoliko je piće s računa već prodano, updateaj prodano piće i reci da to piće već postoji
                if (chosenDrink.drinkName == soldDrink.soldDrinkName){
                    updateSoldDrink(chosenDrink, soldDrink, soldDrinkDao)
                    exists = true
                }
            }
            //Ako piće ne postoji u listi prodanih pića, unesi ga u bazu podataka
            if (exists == false){
                insertToSoldDrinks(chosenDrink, soldDrinkDao)
            }
            //Vrati vrijednost existsa na false kako bi nova iteracija vratila ispravnu vrijednost
            exists = false
        }
    }

    //Funkcija za unos pića u bazu podataka pomoću korutine
    private fun insertToSoldDrinks(
        chosenDrink: ChosenDrink,
        soldDrinkDao: SoldDrinkDao
    ) {
        billingScope.launch {
            val soldDrink = SoldDrink()

            soldDrink.piecesSold = chosenDrink.noOfChosen
            soldDrink.soldDrinkName = chosenDrink.drinkName

            withContext(Dispatchers.IO){
                soldDrinkDao.insert(soldDrink)
            }
        }
    }

    //Funkcija za update prodanog pića pomoću korutine
    private fun updateSoldDrink(
        chosenDrink: ChosenDrink,
        soldDrink: SoldDrink,
        soldDrinkDao: SoldDrinkDao
    ) {
        billingScope.launch {
            withContext(Dispatchers.IO){
                var drink = soldDrinkDao.get(soldDrink.soldDrinkName)
                drink.piecesSold += chosenDrink.noOfChosen
                soldDrinkDao.update(drink)
            }
        }
    }

    //Funkcija za unos računa u bazu podataka
    private fun insertBillIntoDatabase(
        bill: Bill,
        billDao: BillDao
    ) {
        billingScope.launch {
            withContext(Dispatchers.IO){
                try {
                    billDao.insert(bill)
                }
                catch(e: Exception){
                    return@withContext
                }
            }
        }
    }

    //Funkcija za izračun iznosa računa
    private fun calculateSum(drinksOnBill: ArrayList<ChosenDrink>): Double {
        var sum = 0.00
        for (drink in drinksOnBill){
            sum += drink.price
        }

        return sum
    }


    //Funkcija za generiranje random stinga za broj računa
    fun getRandomString(length: Int) : String {
        val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        return List(length) { charset.random() }
            .joinToString("")
    }

    //Funkcija koja se pokreće kad se dobije rezultat skeniranja printera
    //Potrebno za rad Printootha
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ScanningActivity.SCANNING_FOR_PRINTER && requestCode == Activity.RESULT_OK){
            printPrintables(bill)
        }
    }

}
