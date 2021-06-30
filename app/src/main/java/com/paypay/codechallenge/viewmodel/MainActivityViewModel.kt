package com.paypay.codechallenge.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paypay.codechallenge.database.DatabaseConstants
import com.paypay.codechallenge.models.ParsedExchangeRates
import com.paypay.codechallenge.repository.ExchangeRatesRepository
import com.paypay.codechallenge.repository.SharedPreference
import kotlinx.coroutines.launch
import java.util.*

class MainActivityViewModel : ViewModel() {

    private var selectedSpinnerIndex = -1
    private var repository = ExchangeRatesRepository()
    val lastUpdatedAt: MutableLiveData<Int> = MutableLiveData()
    val currencyCodes: MutableLiveData<List<String>> = MutableLiveData()
    val exchangeRates: MutableLiveData<List<ParsedExchangeRates>> = MutableLiveData()

    fun getCurrencyCodesFromRepo() {
        viewModelScope.launch{
            try{
                currencyCodes.value = repository.getAllCurrencyCodes()
            } catch (e:Exception){
                Log.e("GetCurrencyCodes", "Failure: ${e.message}")
            }
        }
    }

    fun getCalculatedExchangeRates(inputMultipleFactor: Double, inputCurrencyCodePosition: Int) {
        viewModelScope.launch {
            try{
                selectedSpinnerIndex = inputCurrencyCodePosition
                val parsedExchangeRates = repository.getAllExchangeRates(timeDifferenceInMillis())
                val inputCurrencyRate = parsedExchangeRates[inputCurrencyCodePosition].exchangeRate

                parsedExchangeRates.forEach {
                    it.exchangeRate = calculateExchangeRateValue(inputCurrencyRate, it.exchangeRate, inputMultipleFactor)
                }
                exchangeRates.value = parsedExchangeRates
                getLastUpdatedAt()
            } catch (e:Exception){
                Log.d("GetExchangeRates", "Failure: ${e.message}")
            }
        }
    }

    fun getSpinnerSelectionIndex(): Int {
        return when (selectedSpinnerIndex) {
            -1 -> currencyCodes.value!!.indexOf(DatabaseConstants.DEFAULT_CURRENCY)
            else -> selectedSpinnerIndex
        }
    }

    fun calculateExchangeRateValue(inputRate: Double, outputRate: Double, multipleFactor: Double): Double {
        return ((outputRate/inputRate)*multipleFactor)
    }

    fun getLastUpdatedAt() {
        lastUpdatedAt.value = ((timeDifferenceInMillis()/1000)/60).toInt() //** Convert milliseconds to minutes **//
    }

    fun timeDifferenceInMillis(): Long {
        return Date(System.currentTimeMillis()).time - SharedPreference.getLastUpdatedAt()
    }

    fun setMockRepositoryForTesting (repository: ExchangeRatesRepository) {
        this.repository = repository
    }
}