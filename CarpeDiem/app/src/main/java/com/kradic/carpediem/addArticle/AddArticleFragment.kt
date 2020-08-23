package com.kradic.carpediem.addArticle

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.kradic.carpediem.R
import com.kradic.carpediem.database.CarpeDiemDatabase
import com.kradic.carpediem.databinding.FragmentAddArticleBinding


/**
 * A simple [Fragment] subclass.
 */
class AddArticleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val binding: FragmentAddArticleBinding
                = DataBindingUtil.inflate(inflater, R.layout.fragment_add_article, container, false)

        //Inicijalizacija aplikacije, interfacea s bazom, factoryja i ViewModela
        val application = requireNotNull(this.activity).application

        val dataSource = CarpeDiemDatabase.getInstance(application).drinkDao

        val viewModelFactory = AddArticleViewModelFactory(dataSource)

        val addArticleViewModel
                = ViewModelProviders.of(this, viewModelFactory).get(AddArticleViewModel::class.java)

        binding.addArticleViewModel = addArticleViewModel

        //Učitaj pića
        addArticleViewModel.loadDrinks()

        //Inicijaliziraj spinner s kategorijama
        var ddlCategories = binding.catSpinner

        //Postavi iteme kategorija iz resursa Categories
        val items: Array<String> = resources.getStringArray(R.array.Categories)

        //Postavi adapter za spinner
        val adapter = ArrayAdapter(this.activity!!, R.layout.support_simple_spinner_dropdown_item, items)

        ddlCategories.adapter = adapter

        binding.catSpinner.onItemSelectedListener = object : AdapterView.OnItemClickListener,
            AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            //Kad se odabere item
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //Postavi taj item kao kategoriju i prikaži je bijele boje, veličine teksta 15f
                addArticleViewModel.category = parent?.getItemAtPosition(position).toString()
                (parent!!.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                (parent!!.getChildAt(0) as TextView).textSize = 15f
            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }
        }

        //Promatraj je li korisnik kliknuo POTVRDI
        addArticleViewModel.navigateBack.observe(this, Observer {
            //Ako je kliknuo
            if (it == true){
                //Ako artikl postoji, ispiši poruku i ostani na ovom fragmentu
                if (addArticleViewModel.exists){
                    Toast.makeText(context, "Već postoji navedeni artikl", Toast.LENGTH_SHORT).show()
                    addArticleViewModel.stopNavigatingBack()
                }
                //Ako ne postoji, ispiši poruku i vrati se na prethodni ekran
                else{
                    Toast.makeText(context, "Unijeli ste artikl: ${addArticleViewModel.article.value.toString()}", Toast.LENGTH_SHORT)
                        .show()
                    requireActivity().onBackPressed()
                    addArticleViewModel.stopNavigatingBack()
                }
            }
        })

        //Gumb POVRATAK vraća na prethodni ekran
        binding.btnReturn.setOnClickListener{
            requireActivity().onBackPressed()
        }

        return binding.root
    }

}
