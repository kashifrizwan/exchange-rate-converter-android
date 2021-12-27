package com.paypay.codechallenge

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.paypay.codechallenge.adapter.ExchangeRatesAdapter
import com.paypay.codechallenge.databinding.ActivityMainBinding
import com.paypay.codechallenge.models.ParsedExchangeRates
import com.paypay.codechallenge.viewmodel.MainActivityViewModel
import dagger.android.AndroidInjection
import javax.inject.Inject

class MainActivity : AppCompatActivity(), OnItemSelectedListener, TextWatcher {

    //ToDo: Inject activityMainBinding with Dependency Injector
    private lateinit var activityMainBinding: ActivityMainBinding
    @Inject lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AndroidInjection.inject(this)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        activityMainBinding.spinnerInputCurrencyCode.onItemSelectedListener = this
        activityMainBinding.editTextInputCurrencyValue.addTextChangedListener(this)
    }

    override fun onResume() {
        super.onResume()

        mainActivityViewModel.exchangeRates.observe(this, Observer {
            val parsedExchangeRates = it as List<ParsedExchangeRates>
            activityMainBinding.recyclerViewOutputCurrencyList.apply {
                adapter = ExchangeRatesAdapter(parsedExchangeRates)
                layoutManager = LinearLayoutManager(this@MainActivity)
            }
        })

        mainActivityViewModel.currencyCodes.observe(this, Observer {
            val currencyCodesList = it as List<String>
            activityMainBinding.spinnerInputCurrencyCode.apply {
                adapter = ArrayAdapter(this@MainActivity,
                    android.R.layout.simple_spinner_dropdown_item, currencyCodesList)
                setSelection(mainActivityViewModel.getSpinnerSelectionIndex())
            }
        })

        mainActivityViewModel.lastUpdatedAt.observe(this, Observer {
            val lastUpdatedMinutes = it as Int
            activityMainBinding.textViewLastUpdatedAt.text = getString(R.string.last_updated, lastUpdatedMinutes)
        })
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        if(activityMainBinding.editTextInputCurrencyValue.text.isNotEmpty()) {
            val inputValue = activityMainBinding.editTextInputCurrencyValue.text.toString().toDouble()
            mainActivityViewModel.getCalculatedExchangeRates(inputValue, position)
        }
    }

    override fun afterTextChanged(currencyValue: Editable) {
        if(currencyValue.isNotEmpty()) {
            mainActivityViewModel.getCalculatedExchangeRates(currencyValue.toString().toDouble(),
                activityMainBinding.spinnerInputCurrencyCode.selectedItemPosition)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {}

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
}
