package com.paypay.codechallenge.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paypay.codechallenge.database.DatabaseConstants
import com.paypay.codechallenge.domain.ExchangeRatesDomain
import com.paypay.codechallenge.models.ParsedExchangeRates
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivityViewModel @Inject constructor (
    private val exchangeRatesDomain: ExchangeRatesDomain
) : ViewModel() {

    private var selectedSpinnerIndex = -1
    val lastUpdatedAt: MutableLiveData<Int> = MutableLiveData()
    val currencyCodes: MutableLiveData<List<String>> = MutableLiveData()
    val exchangeRates: MutableLiveData<List<ParsedExchangeRates>> = MutableLiveData()

    init {
        Log.d("ExchangeRatesApp", "View Model Initialized")
        getCurrencyCodes()
    }

    private fun getCurrencyCodes() {
        viewModelScope.launch {
            try{
                currencyCodes.value = exchangeRatesDomain.getCurrencyCodes()
            } catch (e: Exception){
                Log.e("GetCurrencyCodes", "Failure: ${e.message}")
            }
        }
    }

    fun getCalculatedExchangeRates(inputMultipleFactor: Double, inputCurrencyCodePosition: Int) {
        selectedSpinnerIndex = inputCurrencyCodePosition
        viewModelScope.launch {
            try{
                exchangeRates.value = exchangeRatesDomain.getCalculatedExchangeRates(
                    exchangeRates.value,
                    inputMultipleFactor,
                    inputCurrencyCodePosition
                )
                lastUpdatedAt.value = exchangeRatesDomain.getLastUpdatedAtValue()
            } catch (e: Exception){
                Log.e("GetExchangeRates", "Failure: ${e.message}")
            }
        }
    }

    fun getSpinnerSelectionIndex(): Int {
            return when (selectedSpinnerIndex) {
            -1 -> currencyCodes.value?.indexOf(DatabaseConstants.DEFAULT_CURRENCY) ?: -1
            else -> selectedSpinnerIndex
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("ExchangeRatesApp", "View Model Cleared")
    }
}