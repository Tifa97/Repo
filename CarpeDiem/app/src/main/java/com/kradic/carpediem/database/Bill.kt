package com.kradic.carpediem.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "bill_table", indices = arrayOf(Index(value = ["bill"], unique = true)))
data class Bill (
    @PrimaryKey(autoGenerate = true)
    var billId: Long = 0L,

    @ColumnInfo(name = "bill")
    var billNumber: String = "",

    @ColumnInfo(name = "time")
    var time: String = "",

    @ColumnInfo(name = "amount")
    var amount: Double = -1.00
)