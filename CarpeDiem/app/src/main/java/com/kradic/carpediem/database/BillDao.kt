package com.kradic.carpediem.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BillDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(bill: Bill)

    @Query("Select * from bill_table")
    fun getAllBills(): LiveData<List<Bill>>

    @Query("Select * from bill_table")
    fun getAllBillsList(): List<Bill>

    @Query("SELECT billId FROM bill_table ORDER BY billId DESC LIMIT 1")
    fun getNoOfBills(): Long

    @Query("SELECT * FROM bill_table ORDER BY billId DESC LIMIT 1")
    fun getLastBill(): Bill?

    @Query("DELETE FROM bill_table WHERE billId=(SELECT MAX(billId) FROM bill_table)")
    fun deleteLastBill()

    @Query("delete from bill_table")
    fun clear()
}