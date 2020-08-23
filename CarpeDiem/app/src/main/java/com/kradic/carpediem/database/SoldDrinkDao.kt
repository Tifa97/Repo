package com.kradic.carpediem.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SoldDrinkDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(soldDrink: SoldDrink)

    @Update
    fun update(soldDrink: SoldDrink)

    @Query("delete from sold_drink_table")
    fun clear()

    @Query("Select * from sold_drink_table order by pieces_sold desc")
    fun getAllSoldDrinks(): LiveData<List<SoldDrink>>

    @Query("Select * from sold_drink_table")
    fun getAllSoldDrinksList(): List<SoldDrink>

    @Query("select * from sold_drink_table where sold_drink = :key")
    fun get(key: String): SoldDrink
}