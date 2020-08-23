package com.kradic.carpediem.editArticle

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.kradic.carpediem.*
import com.kradic.carpediem.database.CarpeDiemDatabase

import com.kradic.carpediem.databinding.FragmentEditArticleBinding
import kotlinx.android.synthetic.main.fragment_add_article.*

/**
 * A simple [Fragment] subclass.
 */
class EditArticleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentEditArticleBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_article, container, false)

        //Inicijalizacija aplikacije, interfacea s bazom, factoryja i ViewModela
        val application = requireNotNull(this.activity).application

        val arguments = EditArticleFragmentArgs.fromBundle(arguments!!)

        val dataSource = CarpeDiemDatabase.getInstance(application).drinkDao

        val viewModelFactory = EditArticleViewModelFactory(arguments.drinkId, dataSource)

        val editArticleViewModel
                = ViewModelProviders.of(this, viewModelFactory).get(EditArticleViewModel::class.java)

        binding.editArticleViewModel = editArticleViewModel


        //Inicijaliziraj spinner s kategorijama
        var ddlCategories = binding.catSpinner

        //Postavi iteme kategorija iz resursa Categories
        val items: Array<String> = resources.getStringArray(R.array.Categories)

        //Postavi adapter za spinner
        val adapter = ArrayAdapter(this.activity!!, R.layout.support_simple_spinner_dropdown_item, items)

        ddlCategories.adapter = adapter


        binding.catSpinner.onItemSelectedListener = object : AdapterView.OnItemClickListener,
            AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            //Kad se odabere item
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //Postavi boju i veličinu teksta
                (parent!!.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                (parent.getChildAt(0) as TextView).textSize = 15f
                //Ako odabrani item nije "Odaberite kategoriju", postavi item kao kategoriju
                if (parent.getItemAtPosition(position).toString() != "Odaberite kategoriju"){
                    editArticleViewModel.category = parent.getItemAtPosition(position).toString()
                }
            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }
        }

        //Promatraj piće i postavi njegove vrijednosti u layout
        editArticleViewModel.drink.observe(this, Observer {
            binding.articleToEdit.text = it.drink
            binding.etCijena.hint = it.price.toString()
        })

        //Promatraj je li korisnik kliknuo POVRATAK
        editArticleViewModel.navigateBack.observe(this, Observer {
            //Ako je, vrati na prethodni ekran
            if(it == true){
                requireActivity().onBackPressed()
                editArticleViewModel.stopNavigating()
            }
        })

        //Promatraj je li korisnik kliknuo POTVRDI
        editArticleViewModel.update.observe(this, Observer {
            //Ako je, ispiši poruku i vrati na prethodni ekran
            if (it == true){
                Toast.makeText(context,
                    "Artikl " + editArticleViewModel.drink.value?.drink + " uređen", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressed()
                editArticleViewModel.stopUpdating()
            }
        })

        return binding.root
    }

}
