package com.kradic.carpediem.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sold_drink_table")
data class SoldDrink(
    @PrimaryKey(autoGenerate = true)
    var soldDrinkId: Long = 0L,

    @ColumnInfo(name = "sold_drink")
    var soldDrinkName: String = "",

    @ColumnInfo(name = "pieces_sold")
    var piecesSold: Int = 0

)
