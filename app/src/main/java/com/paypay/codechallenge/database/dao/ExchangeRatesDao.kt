package com.paypay.codechallenge.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.paypay.codechallenge.models.ParsedExchangeRates

@Dao
interface ExchangeRatesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(parsedExchangeRates: List<ParsedExchangeRates>)

    @Query("SELECT * FROM ParsedExchangeRates")
    suspend fun getAllExchangeRates(): List<ParsedExchangeRates>

    @Query("SELECT currency_code FROM ParsedExchangeRates")
    suspend fun getCurrencyCodesList(): List<String>
}