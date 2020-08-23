package com.kradic.carpediem.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel(userName: String, isAdmin: Boolean) : ViewModel() {
    //MutableLiveData varijable čija vrijednost se promatra u fragmentu
    private val _name = MutableLiveData<String>()
    val name: LiveData<String>
        get() = _name

    private val _adminStatus = MutableLiveData<Boolean>()
    val adminStatus: LiveData<Boolean>
        get() = _adminStatus

    private val _navigateToBilling = MutableLiveData<Boolean?>()
    val navigateToBilling: LiveData<Boolean?>
        get() = _navigateToBilling

    private val _navigateToBills = MutableLiveData<Boolean?>()
    val navigateToBills: LiveData<Boolean?>
        get() = _navigateToBills

    private val _navigateToSettings = MutableLiveData<Boolean?>()
    val navigateToSettings: LiveData<Boolean?>
        get() = _navigateToSettings

    private val _navigateToWrite = MutableLiveData<Boolean?>()
    val navigateToWrite: LiveData<Boolean?>
        get() = _navigateToWrite

    private val _navigateToArticles = MutableLiveData<Boolean?>()
    val navigateToArticles: LiveData<Boolean?>
        get() = _navigateToArticles

    //Inicijalizavija userNamea i isAdmin statusa
    init {
        _name.value = userName
        _adminStatus.value = isAdmin
    }

    //Funkcije za navigaciju na druge ekrane
    fun startNavigatingToBilling(){
        _navigateToBilling.value = true
    }

    fun stopNavigatingToBilling(){
        _navigateToBilling.value = false
    }

    fun startNavigatingToBills(){
        _navigateToBills.value = true
    }

    fun stopNavigatingToBills(){
        _navigateToBills.value = false
    }

    fun startNavigatingToSettings(){
        _navigateToSettings.value = true
    }

    fun stopNavigatingToSettings(){
        _navigateToSettings.value = false
    }

    fun startNavigatingToWrite(){
        _navigateToWrite.value = true
    }

    fun stopNavigatingToWrite(){
        _navigateToWrite.value = false
    }

    fun startNavigatingToArticles(){
        _navigateToArticles.value = true
    }

    fun stopNavigatingToArticles(){
        _navigateToArticles.value = false
    }

}