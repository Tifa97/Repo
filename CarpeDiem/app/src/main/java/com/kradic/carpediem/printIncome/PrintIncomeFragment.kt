package com.kradic.carpediem.printIncome

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.kradic.carpediem.MainActivity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

import com.kradic.carpediem.R
import com.kradic.carpediem.database.BillDao
import com.kradic.carpediem.database.CarpeDiemDatabase
import com.kradic.carpediem.database.SoldDrink
import com.kradic.carpediem.database.SoldDrinkDao
import com.kradic.carpediem.databinding.FragmentPrintIncomeBinding
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.RawPrintable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback
import kotlinx.android.synthetic.main.fragment_daily_income.*

/**
 * A simple [Fragment] subclass.
 */
class PrintIncomeFragment : Fragment() {

    //Printing varijabla potrebna za korištenje Printootha
    private var printing : Printing? = null

    //String dnevnog prometa
    private var income: String = ""
    //Varijabla za listu prodanih pića
    private var soldDrinks: List<SoldDrink>? = emptyList()
    //Lozinka za printanje prometa
    private var pass: String = "2020"

    //MutableLiveData varijabla koja prati uspješnost printanja
    private val _success = MutableLiveData<Boolean?>()
    val success: LiveData<Boolean?>
        get() = _success

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentPrintIncomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_print_income, container, false)

        //Inicijalizacija aplikacije, interfacea s bazom, factoryja (prima dnevni promet iz DailyIncomeFragmenta) i ViewModela
        val application = requireNotNull(activity as MainActivity).application
        val soldDrinkDao: SoldDrinkDao = CarpeDiemDatabase.getInstance(application).soldDrinkDao
        val billDao: BillDao = CarpeDiemDatabase.getInstance(application).billDao

        val printIncomeViewModelFactory
                = PrintIncomeViewModelFactory(PrintIncomeFragmentArgs.fromBundle(arguments!!).total, soldDrinkDao, billDao)

        val printIncomeViewModel
                = ViewModelProviders.of(this, printIncomeViewModelFactory).get(PrintIncomeViewModel::class.java)

        //U String varijablu income spremi proslijeđeni argument iz ViewModela
        income = printIncomeViewModel.income

        //Promatraj listu prodanih pića iz ViewModela i postavi je u  varijablu soldDrinks
        printIncomeViewModel.soldDrinks.observe(this, Observer {
            it?.let {
                soldDrinks = it
            }
        })

        //Provjera uparenosti s printerom, potrebno za Printooth
        if (Printooth.hasPairedPrinter()){
            printing = Printooth.printer()
        }

        //Poziv funkcije za inicijalizaciju Printooth i layout listenera
        initListeners(binding, printIncomeViewModel)

        return binding.root
    }

    //Funkcija za inicijalizaciju listenera
    private fun initListeners(binding: FragmentPrintIncomeBinding, printIncomeViewModel: PrintIncomeViewModel) {
        //Klik na gumb POTVRDI
        binding.btnConfirm.setOnClickListener{
            //Ako je unesena ispravna lozinka
            if (binding.etPassword.text.toString() == pass){
                //Ako printer nije uparen, pokreni aktivnost za uparivanje
                if(!Printooth.hasPairedPrinter()){
                    startActivityForResult(Intent(context, ScanningActivity::class.java), ScanningActivity.SCANNING_FOR_PRINTER)
                }
                //Ako je printer uparen
                else{
                    //Printaj promet
                    printPrintables()
                    //Promatraj je li printanje bilo uspješno
                    success.observe(this, Observer {
                        //Ako je uspješno, briši račune i prodana pića iz baze
                        if (it == true){
                            printIncomeViewModel.clearBillsAndSoldDrinks()
                        }
                    })

                }
            }
            //Ako je neispravna lozinka, ispiši poruku
            else{
                Toast.makeText(context, "Neispravna lozinka", Toast.LENGTH_SHORT).show()
            }
        }

        //Gumb POVRATAK vraća na prethodni ekran
        binding.btnReturn.setOnClickListener {
            this.findNavController().navigate(PrintIncomeFragmentDirections.actionPrintIncomeFragmentToDailyIncomeFragment())
        }

        //Printooth listeneri
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
                //Uspješno printanje
                _success.value = true
                Toast.makeText(context, "Order sent to printer", Toast.LENGTH_SHORT).show()
            }

        }
    }

    //Funkcija za ispis prometa
    private fun printPrintables() {
        //Dohvati ArrayListu Printablea koji se moraju naći na slipu dnevnog prometa
        val printables: ArrayList<Printable> = getPrintables(income, soldDrinks)
        //Printaj Printablese
        printing?.print(printables)
    }

    //Funkcija koja određuje izgled računa
    private fun getPrintables(income: String, soldDrinks: List<SoldDrink>?) = ArrayList<Printable>().apply {
        add(RawPrintable.Builder(byteArrayOf(27, 100, 4)).build())

        add(
            TextPrintable.Builder()
                .setText("Dnevni promet")
                .setFontSize(DefaultPrinter.FONT_SIZE_LARGE)
                .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setNewLinesAfter(1)
                .build())

        soldDrinks?.forEach{
            add(TextPrintable.Builder()
                .setText("${it.soldDrinkName}                             ${it.piecesSold}x \n--------------------------------")
                .setAlignment(DefaultPrinter.Companion.ALIGNMENT_RIGHT)
                .setCharacterCode(DefaultPrinter.Companion.CHARCODE_PC1252)
                .setNewLinesAfter(1)
                .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                .setLineSpacing(DefaultPrinter.LINE_SPACING_30)
                .build()
            )
        }


        add(TextPrintable.Builder()
            .setText("Dnevni iznos:         $income kn")
            .setNewLinesAfter(2)
            .build())
    }

    //Funkcija koja se pokreće kad se dobije rezultat skeniranja printera
    //Potrebno za rad Printootha
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ScanningActivity.SCANNING_FOR_PRINTER && requestCode == Activity.RESULT_OK){
            printPrintables()
        }
    }

}
