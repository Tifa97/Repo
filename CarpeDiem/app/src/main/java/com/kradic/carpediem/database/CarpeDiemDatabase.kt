package com.kradic.carpediem.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class, Drink::class, Bill::class, SoldDrink::class], version = 4, exportSchema = false)
abstract class CarpeDiemDatabase: RoomDatabase() {

    abstract val userDao: UserDao
    abstract val drinkDao: DrinkDao
    abstract val soldDrinkDao: SoldDrinkDao
    abstract val billDao: BillDao

    companion object{

        @Volatile
        private var INSTANCE: CarpeDiemDatabase? = null

        fun getInstance(context: Context): CarpeDiemDatabase{
            synchronized(this){
                var instance = INSTANCE

                if (instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        CarpeDiemDatabase::class.java,
                        "carpe_diem_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

}