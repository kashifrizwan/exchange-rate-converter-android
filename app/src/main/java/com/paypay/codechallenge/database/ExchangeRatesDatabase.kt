package com.paypay.codechallenge.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.paypay.codechallenge.database.dao.ExchangeRatesDao
import com.paypay.codechallenge.models.ParsedExchangeRates

@Database(entities = [ParsedExchangeRates::class], version = 1)
abstract class ExchangeRatesDatabase : RoomDatabase() {

    abstract fun getExchangeRatesDao(): ExchangeRatesDao
}