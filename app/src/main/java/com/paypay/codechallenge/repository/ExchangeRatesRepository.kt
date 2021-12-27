package com.paypay.codechallenge.repository

import com.paypay.codechallenge.database.dao.ExchangeRatesDao
import com.paypay.codechallenge.models.ParsedExchangeRates
import com.paypay.codechallenge.network.NetworkConstants
import com.paypay.codechallenge.network.RetrofitApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExchangeRatesRepository @Inject constructor(
    private val api: RetrofitApi,
    private val exchangeRatesDao: ExchangeRatesDao
) {

    suspend fun fetchExchangeRatesFromApi() = coroutineScope {
        withContext(Dispatchers.IO) {
            api.getAllExchangeRates(NetworkConstants.CURRENCY_LAYER_API_ACCESS_KEY)
        }
    }

    suspend fun insertExchangeRatesToDB(parsedExchangeRates: List<ParsedExchangeRates>) {
        withContext(Dispatchers.IO) {
            exchangeRatesDao.insert(parsedExchangeRates)
        }
    }

    suspend fun getAllExchangeRatesFromDB() = coroutineScope {
        withContext(Dispatchers.IO) {
            exchangeRatesDao.getAllExchangeRates()
        }
    }

    suspend fun getAllCurrencyCodesFromDB() = coroutineScope {
        withContext(Dispatchers.IO) {
            exchangeRatesDao.getCurrencyCodesList()
        }
    }
}