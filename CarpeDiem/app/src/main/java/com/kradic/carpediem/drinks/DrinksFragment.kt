package com.kradic.carpediem.drinks

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.kradic.carpediem.MainActivity

import com.kradic.carpediem.R
import com.kradic.carpediem.database.CarpeDiemDatabase
import com.kradic.carpediem.databinding.FragmentDrinksBinding
import kotlinx.android.synthetic.main.fragment_drinks.*

/**
 * A simple [Fragment] subclass.
 */
class DrinksFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentDrinksBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_drinks, container, false)

        //Inicijalizacija aplikacije, interfacea s bazom, factoryja (prima kategoriju iz BillingFragmenta) i ViewModela
        val application = requireNotNull(this.activity).application

        val dataSource = CarpeDiemDatabase.getInstance(application).drinkDao

        val drinksViewModelFactory = DrinksViewModelFactory(dataSource,
            DrinksFragmentArgs.fromBundle(arguments!!).category)

        val drinksViewModel
                = ViewModelProviders.of(this, drinksViewModelFactory).get(DrinksViewModel::class.java)

        binding.drinksViewModel = drinksViewModel

        //Postavi adapter za listu pića
        val adapter = DrinkAdapter(DrinkListener { drinkId ->
            drinksViewModel.onDrinkClicked(drinkId)
        })

        binding.drinksList.adapter = adapter

        //adapter promatra listu pića i po promjeni liste se prilagođava i prikazuje promjene
        drinksViewModel.drinks.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        //Adapter za odabrana pića
        val chosenAdapter = ChosenDrinkAdapter()

        //Promatraj koje piće je kliknuto i postavi ga kao odabrano piće
        drinksViewModel.clickedDrinkId.observe(this, Observer {drink ->
            drink?.let {
                drinksViewModel.setClickedDrink(drinksViewModel.clickedDrinkId.value!!)
                binding.txtChosenDrink.text = drinksViewModel.getClickedDrink()?.drink.toString()
            }
        })

        //Promatraj je li korisnik kliknuo gumb za dodavanje pića
        drinksViewModel.clickedAdd.observe(this, Observer {
            //Ako je kliknut gumb za dodavanje
            if (it == true){
                //Provjeri je li odabrano neko piće
                if (binding.txtChosenDrink.text.isNotBlank()){
                    //Dodaj piće na listu odabranih pića i promijeni adapter za odabrana pića u skladu s promjenom
                    drinksViewModel.addChosenDrinkToList()
                    binding.chosenDrinksList.adapter = chosenAdapter
                    chosenAdapter.submitList(drinksViewModel.chosenDrinks)
                }
                //Završi dodavanje
                drinksViewModel.endAdding()
            }
        })

        //Promatraj je li kliknut gumb za uklanjanje pića
        drinksViewModel.clickedRemove.observe(this, Observer {
            //Ako je kliknut gumb za uklanjanje
            if (it == true){
                //Provjeri je li odabrano neko piće
                if (binding.txtChosenDrink.text.isNotBlank()){
                    //Ukloni jedno odabrano piće i promijeni adapter za odabrana pića u skladu s promjenom
                    drinksViewModel.removeChosenDrinkFromList()
                    binding.chosenDrinksList.adapter = chosenAdapter
                    chosenAdapter.submitList(drinksViewModel.chosenDrinks)
                }
                //Završi uklanjanje
                drinksViewModel.endRemoving()
            }
        })

        //Promatraj je li kliknut gumb za potvrdu
        drinksViewModel.clickedConfirm.observe(this, Observer {
            //Ako je kliknut gumb za potvrdu
            if (it == true){
                //Dodaj sva pića iz liste odabranih pića u listu na MainActivityju
                // kako bi se ta pića mogla prikazivati u BillingFragmentu
                for (drink in drinksViewModel.chosenDrinks) {
                    if (drinkAlreadyChosen(drink) == false){
                        (activity as MainActivity).getListOfChosenDrinks().add(drink)
                    }
                }
                //Po završetku, vrati na prethodni ekran i završi potvrdu
                requireActivity().onBackPressed()
                drinksViewModel.onConfirmClickDone()
            }
        })

        //Klikom na gumb POVRATAK, vrati na prethodni ekran
        binding.btnReturn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        //Manageri koji postavljaju izgled adaptera
        val manager = GridLayoutManager(activity, 3)
        binding.drinksList.layoutManager = manager

        val chosenManager = GridLayoutManager(activity, 1)
        binding.chosenDrinksList.layoutManager = chosenManager

        return binding.root
    }

    //Funkcija koja provjerava je li piće već odabrano
    private fun drinkAlreadyChosen(drink: ChosenDrink): Boolean {
        var exists: Boolean = false

        //Za svako piće u MainActivity listi odabranih pića
        for (d in (activity as MainActivity).getListOfChosenDrinks()) {
            //Provjeri postoji li već to piće
            if (d.drinkName == drink.drinkName){
                //Ako postoji, postavi exists na true i updateaj broj koliko je puta piće odabrano i ukupnu cijenu
                exists = true
                d.noOfChosen += drink.noOfChosen
                d.price += drink.price
            }
        }

        //Vrati postoji li piće
        return exists
    }

}
