package com.kradic.carpediem

import android.widget.Button
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.kradic.carpediem.database.Bill
import com.kradic.carpediem.database.Drink
import com.kradic.carpediem.database.SoldDrink
import com.kradic.carpediem.drinks.ChosenDrink

//Funkcije za ispis teksta unutar recycler viewova

@BindingAdapter("drinkString")
fun Button.setDrinkString(drink: Drink?){
    drink?.let {
        text = drink.drink
    }
}

@BindingAdapter("chosenDrinkString")
fun TextView.setChosenDrinkString(chosenDrink: ChosenDrink?){
    chosenDrink?.let {
        text = chosenDrink.drinkName
    }
}

@BindingAdapter("noOfChosenDrink")
fun TextView.setNoOfChosenDrinks(chosenDrink: ChosenDrink?){
    chosenDrink?.let {
        text = "${chosenDrink.noOfChosen}  kom"
    }
}

@BindingAdapter("billDrinkName")
fun TextView.setBillDrinkName(chosenDrink: ChosenDrink?){
    chosenDrink?.let {
        text = chosenDrink.drinkName
    }
}

@BindingAdapter("billDrinkNo")
fun TextView.setNoOfBillDrink(chosenDrink: ChosenDrink?){
    chosenDrink?.let {
        text = "${chosenDrink.noOfChosen}"
    }
}

@BindingAdapter("billDrinkPrice")
fun TextView.setBillDrinkPrice(chosenDrink: ChosenDrink?){
    chosenDrink?.let {
        text = "${chosenDrink.price} kn"
    }
}

@BindingAdapter("soldDrinkName")
fun TextView.setSoldDrinkName(soldDrink: SoldDrink){
    soldDrink?.let {
        text = soldDrink.soldDrinkName
    }
}

@BindingAdapter("soldDrinkNo")
fun TextView.setSoldDrinkNo(soldDrink: SoldDrink){
    soldDrink?.let {
        text = "${soldDrink.piecesSold} kom"
    }
}

@BindingAdapter("billOrdinalNo")
fun TextView.setBillOrdinalNo(bill: Bill){
    bill?.let {
        text = "${bill.billId}."
    }
}

@BindingAdapter("billNumber")
fun TextView.setBillNumber(bill: Bill){
    bill?.let {
        text = bill.billNumber
    }
}

@BindingAdapter("billTime")
fun TextView.setBillTime(bill: Bill){
    bill?.let {
        text = bill.time
    }
}

@BindingAdapter("billAmount")
fun TextView.setBillAmount(bill: Bill){
    bill?.let {
        text = "${bill.amount} kn"
    }
}

@BindingAdapter("drinkText")
fun TextView.setDrinkString(drink: Drink?){
    drink?.let {
        text = drink.drink
    }
}
