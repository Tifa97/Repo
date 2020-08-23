package com.kradic.carpediem.bills

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager

import com.kradic.carpediem.R
import com.kradic.carpediem.database.CarpeDiemDatabase
import com.kradic.carpediem.databinding.FragmentBillsBinding
import kotlinx.android.synthetic.main.fragment_bills.*

/**
 * A simple [Fragment] subclass.
 */
class BillsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentBillsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_bills, container, false)

        //Inicijalizacija aplikacije, interfacea s bazom, factoryja i ViewModela
        val application = requireNotNull(this.activity).application

        val dataSource = CarpeDiemDatabase.getInstance(application).billDao

        val viewModelFactory = BillsViewModelFactory(dataSource, application)

        val viewModel
                = ViewModelProviders.of(this, viewModelFactory).get(BillsViewModel::class.java)

        binding.billsViewModel = viewModel

        //Adapter za prikazivanje računa
        val adapter = BillAdapter()
        binding.billsList.adapter = adapter

        //Promatraj listu računa i po promjeni potvrdi izgled adaptera i izračunaj dnevni promet
        viewModel.bills.observe(this, Observer {
            it?.let {
                adapter.submitList(it)
                viewModel.calculateTotalDailyIncome()
                txtBillsTotal.text = "%.2f".format(viewModel.billsTotal) + " kn"
            }
        })

        //Klikom na gumb POVRATAK vrati na prethodni ekran
        binding.btnReturn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        //Manager koji postavlja izgled RecyclerViewa
        val manager = GridLayoutManager(activity, 1)
        binding.billsList.layoutManager = manager

        return binding.root
    }

}
