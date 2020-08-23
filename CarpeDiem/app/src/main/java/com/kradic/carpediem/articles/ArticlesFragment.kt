package com.kradic.carpediem.articles

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
import com.kradic.carpediem.database.CarpeDiemDatabase
import com.kradic.carpediem.databinding.FragmentArticlesBinding
import com.kradic.carpediem.drinks.*

/**
 * A simple [Fragment] subclass.
 */
class ArticlesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentArticlesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_articles, container, false)

        //Inicijalizacija aplikacije, interfacea s bazom, factoryja i ViewModela
        val application = requireNotNull(this.activity).application

        val dataSource = CarpeDiemDatabase.getInstance(application).drinkDao

        val articlesViewModelFactory = ArticlesViewModelFactory(dataSource, application)

        val articlesViewModel
                = ViewModelProviders.of(this, articlesViewModelFactory).get(ArticlesViewModel::class.java)

        binding.articlesViewModel = articlesViewModel

        //Adapter za RecyclerView, jednak kao adapter iz DrinksFragmenta
        val adapter = DrinkAdapter(DrinkListener { drinkId ->
            articlesViewModel.onDrinkClicked(drinkId)
        })

        binding.articlesList.adapter = adapter

        articlesViewModel.drinks.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        //Promatraj id kliknutog pića
        articlesViewModel.clickedDrinkId.observe(this, Observer {drink ->
            drink?.let {
                //Postavi piće kao kliknuto
                articlesViewModel.setClickedDrink(articlesViewModel.clickedDrinkId.value!!)
                binding.txtChosenDrink.text = articlesViewModel.getClickedDrink()?.drink.toString()
            }
        })

        //Promatraj je li korisnik kliknuo UREDI
        articlesViewModel.clickedEdit.observe(this, Observer {
            //Ako je kliknuo
            if (it == true){
                //Ako je odabrano neko piće, preusmjeri na EditArticleFragment s id-jem odabranog pića kao argumentom
                if (articlesViewModel.clickedDrinkId.value != null){
                    this.findNavController().navigate(ArticlesFragmentDirections.actionArticlesFragmentToEditArticleFragment(
                        articlesViewModel.clickedDrinkId.value!!
                    ))
                    articlesViewModel.stopNavigatingToEdit()
                }
            }
        })

        //Promatraj je li korisnik kliknuo DODAJ
        articlesViewModel.clickedAdd.observe(this, Observer {
            //Ako je kliknuo, preusmjeri na AddArticleFragment
            if (it == true){
                this.findNavController().navigate(ArticlesFragmentDirections.actionArticlesFragmentToAddArticleFragment())

                articlesViewModel.stopNavigatingToAdd()
            }
        })

        //Gumb POVRATAK vraća na prethodni ekran
        binding.btnReturn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        //Manager koji određuje izgled RecyclerViewa
        val manager = GridLayoutManager(activity, 3)
        binding.articlesList.layoutManager = manager

        return binding.root
    }

}
