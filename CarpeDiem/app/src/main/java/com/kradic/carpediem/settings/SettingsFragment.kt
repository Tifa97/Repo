package com.kradic.carpediem.settings

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.kradic.carpediem.MainActivity

import com.kradic.carpediem.R
import com.kradic.carpediem.databinding.FragmentSettingsBinding
import kotlinx.android.synthetic.main.fragment_settings.*

/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : Fragment() {

    //Varijable za omogućivanje popusta i preračuna u eure
    var isDiscountChecked: Boolean = false
    var isEurChecked: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentSettingsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)

        //Binding Viewova iz layouta s varijablama iz MainActivityja kako bi se mogle koristiti u BillingFragmentu
        binding.cbEnableDiscount.isChecked = (activity as MainActivity).enableDiscount
        binding.cbChangeToEur.isChecked = (activity as MainActivity).enableEuro
        binding.etEur.hint = (activity as MainActivity).euro.toString()

        //listener za popust
        binding.cbEnableDiscount.setOnClickListener(){
            //Ako je označen check box za popust, dozvoli popust i postavi vrijednost u MainActivityju
            if (cbEnableDiscount.isChecked){
                isDiscountChecked = true
                (activity as MainActivity).enableDiscount = isDiscountChecked
            }
            //Ako nije, zabrani popust i postavi vrijednost u MainActivityju
            else{
                (activity as MainActivity).enableDiscount = isDiscountChecked
            }
        }

        //listener za preračun u eure
        binding.cbChangeToEur.setOnClickListener(){
            //Ako je označen check box za preračun, dozvoli preračun i postavi vrijednost u MainActivityju
            if (cbChangeToEur.isChecked){
                isEurChecked = true
                (activity as MainActivity).enableEuro = isEurChecked
            }
            //Ako nije, zabrani preračun i postavi vrijednost u MainActivityju
            else{
                (activity as MainActivity).enableEuro = isEurChecked
            }
        }

        //Potvrdi promjene klikom na POTVRDI
        binding.btnPotvrdi.setOnClickListener{
            if(binding.etEur.text.isNotBlank()){
                (activity as MainActivity).euro = binding.etEur.text.toString().toDouble()
            }
            requireActivity().onBackPressed()
        }

        //Klikom na POVRATAK vrati na prethodni ekran
        binding.btnReturn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return binding.root
    }

    //Spremi stanje varijabli za popust i preračun u eure
    override fun onSaveInstanceState(outState: Bundle) {
        outState?.run {
            outState.putBoolean("discount", isDiscountChecked)
            outState.putBoolean("eur", isEurChecked)
        }

        super.onSaveInstanceState(outState)
    }

    //Override onCreate funkcije
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Ako postoji spremljeno stanje, postavi varijable u to stanje
        if (savedInstanceState != null){
            with(savedInstanceState){
                isEurChecked = getBoolean("eur")
                isDiscountChecked = getBoolean("discount")
            }
        }
        //U suprotnom, varijable su false
        else{
            isEurChecked = false
            isDiscountChecked = false
        }
    }

}
