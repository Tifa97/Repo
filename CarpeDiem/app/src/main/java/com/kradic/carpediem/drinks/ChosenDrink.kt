package com.kradic.carpediem.drinks

//Klasa za odabrano piće
data class ChosenDrink (
    var drinkName: String,
    var price: Double,
    var noOfChosen: Int = 1
)