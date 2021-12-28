package com.paypay.codechallenge.domain

import com.paypay.codechallenge.models.ParsedExchangeRates
import com.paypay.codechallenge.repository.ExchangeRatesRepository
import com.paypay.codechallenge.repository.SharedPreference
import java.util.*
import java.util.stream.Collectors
import javax.inject.Inject
import kotlin.Comparator

class ExchangeRatesDomain @Inject constructor(
    private val sharedPreference: SharedPreference,
    private val repository: ExchangeRatesRepository
) {

    suspend fun getCurrencyCodes(): List<String> {
        return if (sharedPreference.getLastUpdatedAt() == 0L) {
            fetchExchangeRatesFromApi().stream().map(ParsedExchangeRates::currencyCode)
                .collect(Collectors.toList())
        } else {
            repository.getAllCurrencyCodesFromDB()
        }
    }

    suspend fun getCalculatedExchangeRates(
        existingExchangeRates: List<ParsedExchangeRates>?,
        inputMultipleFactor: Double,
        inputCurrencyCodePosition: Int
    ) : List<ParsedExchangeRates> {

        val parsedExchangeRates = existingExchangeRates ?: getExchangeRates()
        val inputCurrencyRate = parsedExchangeRates[inputCurrencyCodePosition].exchangeRate
        parsedExchangeRates.forEach {
            it.exchangeRate = calculateExchangeRateValue(inputCurrencyRate, it.exchangeRate, inputMultipleFactor)
        }
        return parsedExchangeRates
    }

    private suspend fun getExchangeRates(): List<ParsedExchangeRates> {
        return if (isExchangeRatesOutdated(timeDifferenceInMillis())) {
            fetchExchangeRatesFromApi()
        } else {
            repository.getAllExchangeRatesFromDB()
        }
    }

    private suspend fun fetchExchangeRatesFromApi(): List<ParsedExchangeRates> {
        val parsedExchangeRates = ArrayList<ParsedExchangeRates>()
        val liveExchangeRates = repository.fetchExchangeRatesFromApi()

        liveExchangeRates.quotes.forEach { (currencyCode, exchangeRate) ->
            parsedExchangeRates.add(ParsedExchangeRates(currencyCode.substring(3), exchangeRate))
        }

        parsedExchangeRates.sortWith(Comparator { obj1, obj2 ->
            obj1.currencyCode.compareTo(obj2.currencyCode)
        })

        repository.insertExchangeRatesToDB(parsedExchangeRates)
        sharedPreference.saveLastUpdatedAt(Date(System.currentTimeMillis()).time)
        return parsedExchangeRates
    }

    fun getLastUpdatedAtValue(): Int {
        return ((timeDifferenceInMillis()/1000)/60).toInt() //** Convert milliseconds to minutes **//
    }

    private fun calculateExchangeRateValue(inputRate: Double, outputRate: Double, multipleFactor: Double): Double {
        return ((outputRate/inputRate)*multipleFactor)
    }

    private fun timeDifferenceInMillis(): Long {
        return Date(System.currentTimeMillis()).time - sharedPreference.getLastUpdatedAt()
    }

    private fun isExchangeRatesOutdated(timeDifferenceInMillis: Long): Boolean {
        return timeDifferenceInMillis > 1800000 //** 1800000 milliseconds are 30 minutes **//
    }
}