package com.kradic.carpediem.login

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kradic.carpediem.database.User
import com.kradic.carpediem.database.UserDao
import kotlinx.coroutines.*

class LoginViewModel(
    val database: UserDao,
    application: Application
) : AndroidViewModel(application){

    //Interface za komunikaciju s bazom
    private val userDao = database

    //LiveData lista korisnika koju čitamo iz baze i prazna lista u koju ćemo učitati korisnike
    var usersLive: LiveData<List<User>> = userDao.getAllUsersLive()
    var users: List<User> = emptyList()

    //MutableLiveData koji prati da li je korisnik kliknuo gumb za prijavu
    private val _navigateToHome = MutableLiveData<Boolean?>()
    val navigateToHome: LiveData<Boolean?>
        get() = _navigateToHome

    //Korisničko ime, lozinka i status
    val userName = MutableLiveData<String>()
    val userPassword = MutableLiveData<String>()
    var isAdmin: Boolean = false

    //varijabla za provjeru postojanja korisnika
    var exists: Boolean = false


    //Funkcija koja provjerava postoji li željeni korisnik
    fun checkIfUserExists(): Boolean{
        users.forEach{
            if (it.name == userName.value.toString() && it.password == userPassword.value.toString()){
                isAdmin = it.isAdmin
                exists = true
            }
        }

        return exists
    }

    //Funkcija za postavljanje _navigateToHome na true
    fun startNavigatingHome(){
        _navigateToHome.value = true
    }

    //Funkcija za postavljanje _navigateToHome na false
    fun stopNavigatingHome(){
        _navigateToHome.value = false
    }

    //Funkcija za resetiranje login forme
    fun resetLoginForm(){
        userName.value = null
        userPassword.value = null
        exists = false
    }

}