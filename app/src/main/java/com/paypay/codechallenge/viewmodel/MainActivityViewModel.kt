package com.paypay.codechallenge.viewmodel

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
        getCurrencyCodes()
    }

    private fun getCurrencyCodes() {
        viewModelScope.launch {
            currencyCodes.value = exchangeRatesDomain.getCurrencyCodes()
        }
    }

    fun getCalculatedExchangeRates(inputMultipleFactor: Double, inputCurrencyCodePosition: Int) {
        selectedSpinnerIndex = inputCurrencyCodePosition
        viewModelScope.launch {
            exchangeRates.value = exchangeRatesDomain.getCalculatedExchangeRates(inputMultipleFactor,
                inputCurrencyCodePosition)
            lastUpdatedAt.value = exchangeRatesDomain.getLastUpdatedAtValue()
        }
    }

    fun getSpinnerSelectionIndex(): Int {
            return when (selectedSpinnerIndex) {
            -1 -> currencyCodes.value?.indexOf(DatabaseConstants.DEFAULT_CURRENCY) ?: -1
            else -> selectedSpinnerIndex
        }
    }
}