package com.kradic.carpediem.write

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController

import com.kradic.carpediem.R
import com.kradic.carpediem.databinding.FragmentWriteBinding
import kotlinx.android.synthetic.main.fragment_home.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class WriteFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentWriteBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_write, container, false)

        //Postavi današnji datum za txtDate
        val date = Calendar.getInstance()
        binding.txtDate.text = SimpleDateFormat("dd-MM-yyyy").format(date.time)

        //Klikom na gumb POVRATAK, vrati se na prethodni ekran
        binding.btnReturn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        //Klikom na gumb DA, otiđi na DailyIncomeFragment
        binding.btnDa.setOnClickListener {
            this.findNavController().navigate(WriteFragmentDirections.actionWriteFragmentToDailyIncomeFragment())
        }

        //Klikom na gumb NE, vrati se na prethodni ekran
        binding.btnNe.setOnClickListener{
            requireActivity().onBackPressed()
        }

        return binding.root
    }


}
