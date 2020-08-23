package com.kradic.carpediem.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DrinkDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(drink: Drink)

    @Update
    fun update(drink: Drink)

    @Query("Select * from drink_table where drinkId = :key")
    fun get(key: Long): Drink

    @Query("Delete from drink_table")
    fun clear()

    @Query("delete from drink_table where drinkId = :key")
    fun deleteDrinkWithId(key: Long)

    @Query("select * from drink_table order by drink")
    fun getAllDrinks(): LiveData<List<Drink>>

    @Query("select * from drink_table order by drink")
    fun getAllDrinksList(): List<Drink>

    @Query("select * from drink_table where category = :key")
    fun getDrinksByCategory(key: String) : LiveData<List<Drink>>

    @Query("select * from drink_table where drinkId = :key")
    fun getDrinkById(key: Long): LiveData<Drink>
}