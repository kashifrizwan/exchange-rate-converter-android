package com.paypay.codechallenge.di

import com.paypay.codechallenge.database.ExchangeRatesDatabase
import com.paypay.codechallenge.database.dao.ExchangeRatesDao
import dagger.Module
import dagger.Provides

@Module
class DataModule {

    @Provides
    fun providesExchangeRatesDao(exchangeRatesDatabase: ExchangeRatesDatabase): ExchangeRatesDao {
        return exchangeRatesDatabase.getExchangeRatesDao()
    }
}