package com.kradic.carpediem.dailyIncome

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

import com.kradic.carpediem.R
import com.kradic.carpediem.database.Bill
import com.kradic.carpediem.database.BillDao
import com.kradic.carpediem.database.CarpeDiemDatabase
import com.kradic.carpediem.databinding.FragmentDailyIncomeBinding
import kotlinx.android.synthetic.main.fragment_bills.*
import kotlinx.android.synthetic.main.fragment_daily_income.*
import kotlinx.coroutines.*

/**
 * A simple [Fragment] subclass.
 */
class DailyIncomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentDailyIncomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_daily_income, container, false)

        //Inicijalizacija aplikacije, interfacea s bazom, factoryja i ViewModela
        val application = requireNotNull(this.activity).application

        val drinksDataSource = CarpeDiemDatabase.getInstance(application).soldDrinkDao
        val billsDataSource = CarpeDiemDatabase.getInstance(application).billDao

        val viewModelFactory = DailyIncomeViewModelFactory(drinksDataSource, billsDataSource, application)

        val dailyIncomeViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(DailyIncomeViewModel::class.java)

        binding.dailyIncomeViewModel = dailyIncomeViewModel

        //Adapter za prodana pića
        val adapter = SoldDrinkAdapter()

        binding.soldDrinks.adapter = adapter

        //Adapter promatra listu prodanih pića i prilagođava se s obzirom na promjene
        dailyIncomeViewModel.soldDrinks.observe(this, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        //Promatraj listu računa i izračunaj dnevni promet
        dailyIncomeViewModel.bills.observe(this, Observer {
            it?.let {
                dailyIncomeViewModel.calculateTotalDailyIncome()
                txtDailyIncome.text =  "%.2f".format(dailyIncomeViewModel.totalDailyIncome) + " kn"
            }
        })

        //Promatraj je li korisnik kliknuo ISPIŠI
        dailyIncomeViewModel.navigateToPrint.observe(this, Observer {
            //Ako je, otiđi na PrintIncomeFragment s argumentom koji sadrži tekst dnevnog prometa
            if (it == true){
                this.findNavController()
                    .navigate(DailyIncomeFragmentDirections.actionDailyIncomeFragmentToPrintIncomeFragment("%.2f".format(dailyIncomeViewModel.totalDailyIncome)))
                dailyIncomeViewModel.stopNavigatingToPrint()
            }
        })

        //gumb POVRATAK vraća na prethodni ekran
        binding.btnReturn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        //Manager koji postavlja izgled RecyclerViewa
        val manager = GridLayoutManager(activity, 1)
        binding.soldDrinks.layoutManager = manager

        return binding.root
    }

}
