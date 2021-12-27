package com.paypay.codechallenge.repository

import com.paypay.codechallenge.database.dao.ExchangeRatesDao
import com.paypay.codechallenge.models.ParsedExchangeRates
import com.paypay.codechallenge.network.NetworkConstants
import com.paypay.codechallenge.network.RetrofitApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.util.*
import java.util.stream.Collectors
import javax.inject.Inject
import kotlin.Comparator
import kotlin.collections.ArrayList

open class ExchangeRatesRepository @Inject constructor(
    private val api: RetrofitApi,
    private val sharedPreference: SharedPreference,
    private val exchangeRatesDao: ExchangeRatesDao
) {

    suspend fun getAllCurrencyCodes(): List<String>? {
        return if (sharedPreference.getLastUpdatedAt() == 0L) {
            refreshExchangeRates().stream().map(ParsedExchangeRates::currencyCode)
                .collect(Collectors.toList())
        } else {
            getAllCurrencyCodesFromDB()
        }
    }

    suspend fun getAllExchangeRates(timeDifferenceInMillis: Long): List<ParsedExchangeRates> {
        return if (isExchangeRatesOutdated(timeDifferenceInMillis)) {
            refreshExchangeRates()
        } else {
            getAllExchangeRatesFromDB()
        }
    }

    open suspend fun refreshExchangeRates() : List<ParsedExchangeRates> {
        val parsedExchangeRates = ArrayList<ParsedExchangeRates>()
        val liveExchangeRates = api.getAllExchangeRates(NetworkConstants.CURRENCY_LAYER_API_ACCESS_KEY)

        liveExchangeRates.quotes.forEach { (currencyCode, exchangeRate) ->
            parsedExchangeRates.add(ParsedExchangeRates(currencyCode.substring(3), exchangeRate))
        }

        parsedExchangeRates.sortWith(Comparator { obj1, obj2 ->
            obj1.currencyCode.compareTo(obj2.currencyCode)
        })

        insertExchangeRatesToDB(parsedExchangeRates)
        sharedPreference.saveLastUpdatedAt(Date(System.currentTimeMillis()).time)

        return parsedExchangeRates
    }

    suspend fun insertExchangeRatesToDB(parsedExchangeRates: List<ParsedExchangeRates>) {
        withContext(Dispatchers.Default) {
            exchangeRatesDao.insert(parsedExchangeRates)
        }
    }

    suspend fun getAllExchangeRatesFromDB() = coroutineScope {
        withContext(Dispatchers.Default) {
            exchangeRatesDao.getAllExchangeRates()
        }
    }

    suspend fun getAllCurrencyCodesFromDB() = coroutineScope {
        withContext(Dispatchers.Default) {
            exchangeRatesDao.getCurrencyCodesList()
        }
    }

    open fun isExchangeRatesOutdated(timeDifferenceInMillis: Long): Boolean {
        return timeDifferenceInMillis > 1800000 //** 1800000 milliseconds are 30 minutes **//
    }
}