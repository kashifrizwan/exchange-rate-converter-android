package com.paypay.codechallenge.repository

import com.paypay.codechallenge.database.ExchangeRatesDatabase
import com.paypay.codechallenge.models.ParsedExchangeRates
import com.paypay.codechallenge.network.RetrofitApi
import com.paypay.codechallenge.network.NetworkConstants
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.*
import java.util.stream.Collectors
import javax.inject.Inject
import kotlin.Comparator
import kotlin.collections.ArrayList

open class ExchangeRatesRepository @Inject constructor(
    private val api: RetrofitApi,
    private val sharedPreference: SharedPreference,
    private val exchangeRatesDatabase: ExchangeRatesDatabase?
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
            getAllExchangeRatesFromDB()!!
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
        coroutineScope {
            val databaseOperation = async {
                exchangeRatesDatabase?.getExchangeRatesDao()?.insert(parsedExchangeRates)
            }
            databaseOperation.await()
        }
    }

    private suspend fun getAllExchangeRatesFromDB() = coroutineScope {
        val databaseOperation = async {
            exchangeRatesDatabase?.getExchangeRatesDao()?.getAllExchangeRates()
        }
        databaseOperation.await()
    }

    private suspend fun getAllCurrencyCodesFromDB() = coroutineScope {
        val databaseOperation = async {
            exchangeRatesDatabase?.getExchangeRatesDao()?.getCurrencyCodesList()
        }
        databaseOperation.await()
    }

    open fun isExchangeRatesOutdated(timeDifferenceInMillis: Long): Boolean {
        return timeDifferenceInMillis > 1800000 //** 1800000 milliseconds are 30 minutes **//
    }
}