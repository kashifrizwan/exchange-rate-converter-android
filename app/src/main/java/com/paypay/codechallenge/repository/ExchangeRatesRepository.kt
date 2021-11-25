package com.paypay.codechallenge.repository

import com.paypay.codechallenge.PayPayCodeChallenge
import com.paypay.codechallenge.database.ExchangeRatesDatabase
import com.paypay.codechallenge.models.ParsedExchangeRates
import com.paypay.codechallenge.network.NetworkConstants
import com.paypay.codechallenge.network.RetrofitBuilder
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.*
import java.util.stream.Collectors
import javax.inject.Inject
import kotlin.Comparator
import kotlin.collections.ArrayList


open class ExchangeRatesRepository @Inject constructor() {

    suspend fun getAllCurrencyCodes(): List<String>? {
        return if (SharedPreference.getLastUpdatedAt() == 0L) {
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
            getAllExchangeRatesFromDB()!!
        }
    }

    open suspend fun refreshExchangeRates() : List<ParsedExchangeRates> {
        val parsedExchangeRates = ArrayList<ParsedExchangeRates>()
        val liveExchangeRates = RetrofitBuilder.api.getAllExchangeRates(NetworkConstants.CURRENCY_LAYER_API_ACCESS_KEY)

        liveExchangeRates.quotes.forEach { (currencyCode, exchangeRate) ->
            parsedExchangeRates.add(ParsedExchangeRates(currencyCode.substring(3), exchangeRate))
        }

        parsedExchangeRates.sortWith(Comparator { obj1, obj2 ->
            obj1.currencyCode.compareTo(obj2.currencyCode)
        })

        insertExchangeRatesToDB(parsedExchangeRates)
        SharedPreference.saveLastUpdatedAt(Date(System.currentTimeMillis()).time)

        return parsedExchangeRates
    }

    suspend fun insertExchangeRatesToDB(parsedExchangeRates: List<ParsedExchangeRates>) {
        coroutineScope {
            val databaseOperation = async {
                getDataBaseInstance()?.getExchangeRatesDao()?.insert(parsedExchangeRates)
            }
            databaseOperation.await()
        }
    }

    private suspend fun getAllExchangeRatesFromDB() = coroutineScope {
        val databaseOperation = async {
            getDataBaseInstance()?.getExchangeRatesDao()?.getAllExchangeRates()
        }
        databaseOperation.await()
    }

    private suspend fun getAllCurrencyCodesFromDB() = coroutineScope {
        val databaseOperation = async {
            getDataBaseInstance()?.getExchangeRatesDao()?.getCurrencyCodesList()
        }
        databaseOperation.await()
    }

    open fun isExchangeRatesOutdated(timeDifferenceInMillis: Long): Boolean {
        return timeDifferenceInMillis > 1800000 //** 1800000 milliseconds are 30 minutes **//
    }

    open fun getDataBaseInstance(): ExchangeRatesDatabase? {
        return  ExchangeRatesDatabase.getInstance(PayPayCodeChallenge.context!!)
    }
}