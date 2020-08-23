package com.kradic.carpediem.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drink_table")
data class Drink (
    @PrimaryKey(autoGenerate = true)
    var drinkId: Long = 0L,

    @ColumnInfo(name = "drink")
    var drink: String = "",

    @ColumnInfo(name = "category")
    var category: String = "",

    @ColumnInfo(name = "price")
    var price: Double = -1.00
)