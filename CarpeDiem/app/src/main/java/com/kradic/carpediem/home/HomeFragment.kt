package com.kradic.carpediem.home

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.kradic.carpediem.MainActivity

import com.kradic.carpediem.R
import com.kradic.carpediem.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentHomeBinding
                = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)


        //Postavljanje datuma za txtDate u layoutu
        val date = Calendar.getInstance()
        binding.txtDate.text = SimpleDateFormat("dd-MM-yyyy").format(date.time)

        //Inicijalizacija factoryja (prima userName i isAdmin iz logina) i ViewModela
        val homeViewModelFactory
                = HomeViewModelFactory(HomeFragmentArgs.fromBundle(arguments!!).userName, HomeFragmentArgs.fromBundle(arguments!!).isAdmin)

        val homeViewModel
                = ViewModelProviders.of(this, homeViewModelFactory).get(HomeViewModel::class.java)

        binding.homeViewModel = homeViewModel

        //Promatraj je li korisnik kliknuo gumb START, ukoliko jest, preusmjeri ga na BillingFragment
        homeViewModel.navigateToBilling.observe(this, Observer {
            if (it == true){
                this.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToBillingFragment(
                    homeViewModel.adminStatus.value!!, homeViewModel.name.value.toString()
                ))
                homeViewModel.stopNavigatingToBilling()
            }
        })

        //Promatraj je li korisnik kliknuo gumb RAČUNI, ako je, provjeri da li je admin
        homeViewModel.navigateToBills.observe(this, Observer {
            if (it == true){
                //Ako je admin, preusmjeri ga na BillsFragment
                if (homeViewModel.adminStatus.value == true){
                    this.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToBillsFragment())
                    homeViewModel.stopNavigatingToBills()
                }
                //Ako nije admin, ispiši poruku zabrane
                else{
                    Toast.makeText(context, "Nemate administratorska prava", Toast.LENGTH_SHORT).show()
                    homeViewModel.stopNavigatingToBills()
                }
            }
        })

        //Promatraj je li korisnik kliknuo gumb POSTAVKE, ako je, provjeri da li je admin
        homeViewModel.navigateToSettings.observe(this, Observer {
            if (it == true){
                //Ako je admin, preusmjeri ga na SettingsFragment
                if (homeViewModel.adminStatus.value == true){
                    this.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSettingsFragment())
                    homeViewModel.stopNavigatingToSettings()
                }
                //Ako nije admin, ispiši poruku zabrane
                else{
                    Toast.makeText(context, "Nemate administratorska prava", Toast.LENGTH_SHORT).show()
                    homeViewModel.stopNavigatingToSettings()
                }
            }
        })

        //Promatraj je li korisnik kliknuo gumb PROMET, ako je, provjeri da li je admin
        homeViewModel.navigateToWrite.observe(this, Observer {
            if (it == true){
                //Ako je admin, preusmjeri ga na WriteFragment
                if (homeViewModel.adminStatus.value == true){
                    this.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToWriteFragment())
                    homeViewModel.stopNavigatingToWrite()
                }
                //Ako nije admin, ispiši poruku zabrane
                else{
                    Toast.makeText(context, "Nemate administratorska prava", Toast.LENGTH_SHORT).show()
                    homeViewModel.stopNavigatingToWrite()
                }
            }
        })

        //Promatraj je li korisnik kliknuo gumb ARTIKLI, ako je, provjeri da li je admin
        homeViewModel.navigateToArticles.observe(this, Observer {
            if (it == true){
                //Ako je admin, preusmjeri ga na ArticlesFragment
                if (homeViewModel.adminStatus.value == true){
                    this.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToArticlesFragment())
                    homeViewModel.stopNavigatingToArticles()
                }
                //Ako nije admin, ispiši poruku zabrane
                else{
                    Toast.makeText(context, "Nemate administratorska prava", Toast.LENGTH_SHORT).show()
                    homeViewModel.stopNavigatingToArticles()
                }
            }
        })

        //Ako korisnik klikne gumb UGASI APLIKACIJU, ugasi aplikaciju
        binding.btnTurnOffDevice.setOnClickListener {
            activity?.finishAffinity()
            exitProcess(0)
        }

        //Postavi OptionsMenu
        setHasOptionsMenu(true)

        return binding.root
    }

    //Stvori OptionsMenu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.options_menu, menu)
    }

    //Funkcija za handlanje OptionsItemima
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        //Ukoliko je kliknuto Odjavi se, vrati na LoginFragment
        when(id){
            R.id.btnLogOut -> this.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
        }
        return super.onOptionsItemSelected(item)
    }

}
