package com.kradic.carpediem.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.kradic.carpediem.login.LoginFragmentDirections

import com.kradic.carpediem.R
import com.kradic.carpediem.database.CarpeDiemDatabase
import com.kradic.carpediem.databinding.FragmentLoginBinding
import kotlin.math.log

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val binding: FragmentLoginBinding
                = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)

        //Inicijalizacija aplikacije, interfacea s bazom, factoryja i ViewModela
        val application = requireNotNull(this.activity).application

        val dataSource = CarpeDiemDatabase.getInstance(application).userDao

        val viewModelFactory = LoginViewModelFactory(dataSource, application)

        val loginViewModel
                = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel::class.java)

        binding.loginViewModel = loginViewModel

        //Promatraj LiveData listu korisnika u ViewModelu i njen sadržaj ubaci u običnu listu
        loginViewModel.usersLive.observe(this, Observer {
            it?.let {
                loginViewModel.users = it
            }
        })

        //Promatraj da li je korisnik kliknuo gumb prijava
        loginViewModel.navigateToHome.observe(this, Observer {
            //Ako je korisnik kliknuo prijavu, provjeri postoji li željeno korisničko ime i lozinka,
            //Navigiraj na glavni ekran, postavi _navigateToHome na false i očisti login formu
            if (it == true){
                if (loginViewModel.checkIfUserExists()){
                    this.findNavController()
                        .navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment
                            (loginViewModel.userName.value.toString(), loginViewModel.isAdmin))
                    loginViewModel.stopNavigatingHome()
                    loginViewModel.resetLoginForm()
                }
                //Ako korisničko ime i lozinka ne postoje, ispiši poruku i postavi _navigateToHome na false
                else {
                    Toast.makeText(context, "Neispravno korisničko ime ili lozinka", Toast.LENGTH_SHORT).show()
                    loginViewModel.stopNavigatingHome()
                }
            }
        })

        return binding.root
    }

}
