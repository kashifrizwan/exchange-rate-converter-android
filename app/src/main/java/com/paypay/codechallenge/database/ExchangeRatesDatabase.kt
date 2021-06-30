package com.paypay.codechallenge.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.paypay.codechallenge.database.dao.ExchangeRatesDao
import com.paypay.codechallenge.models.ParsedExchangeRates

@Database(entities = [ParsedExchangeRates::class], version = 1)
abstract class ExchangeRatesDatabase : RoomDatabase() {

    abstract fun getExchangeRatesDao(): ExchangeRatesDao

    companion object{

        @Volatile
        var databaseInstance: ExchangeRatesDatabase ?= null

        fun getInstance(context: Context): ExchangeRatesDatabase? {
            if(databaseInstance == null) {
                synchronized(ExchangeRatesDatabase::class.java) {
                    databaseInstance= Room.databaseBuilder(context, ExchangeRatesDatabase::class.java,
                        DatabaseConstants.DATABASE_NAME).build()
                }
            }
            return databaseInstance
        }
    }
}