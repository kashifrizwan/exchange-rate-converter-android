package com.paypay.codechallenge.di

import android.util.Log
import com.paypay.codechallenge.database.ExchangeRatesDatabase
import com.paypay.codechallenge.database.dao.ExchangeRatesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class DataModule {

    @Provides
    fun providesExchangeRatesDao(exchangeRatesDatabase: ExchangeRatesDatabase): ExchangeRatesDao {
        Log.d("ExchangeRateApp", "Exchange Rates DAO Created")
        return exchangeRatesDatabase.getExchangeRatesDao()
    }
}