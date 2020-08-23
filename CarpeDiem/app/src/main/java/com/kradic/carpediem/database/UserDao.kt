package com.kradic.carpediem.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(user: User)

    @Update
    fun update(user: User)

    @Query("Select * from user_table where userId = :key")
    fun get(key: Long): User?

    @Query("Delete from user_table")
    fun clear()

    @Query("Select * from user_table order by userId desc")
    fun getAllUsers(): List<User>

    @Query("Select * from user_table order by userId desc")
    fun getAllUsersLive(): LiveData<List<User>>
}