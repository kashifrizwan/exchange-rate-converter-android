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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.paypay.codechallenge.adapter.ExchangeRatesAdapter
import com.paypay.codechallenge.databinding.ActivityMainBinding
import com.paypay.codechallenge.models.ParsedExchangeRates
import com.paypay.codechallenge.viewmodel.MainActivityViewModel

class MainActivity : AppCompatActivity(), OnItemSelectedListener, TextWatcher {

    private lateinit var binding: ActivityMainBinding
    private lateinit var activityMainViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.spinnerInputCurrencyCode.onItemSelectedListener = this
        binding.editTextInputCurrencyValue.addTextChangedListener(this)

        activityMainViewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        activityMainViewModel.getCurrencyCodesFromRepo()

        activityMainViewModel.exchangeRates.observe(this, Observer {
            val parsedExchangeRates = it as List<ParsedExchangeRates>
            binding.recyclerViewOutputCurrencyList.apply {
                adapter = ExchangeRatesAdapter(parsedExchangeRates)
                layoutManager = LinearLayoutManager(this@MainActivity)
            }
        })

        activityMainViewModel.currencyCodes.observe(this, Observer {
            val currencyCodesList = it as List<String>
            binding.spinnerInputCurrencyCode.apply {
                adapter = ArrayAdapter(this@MainActivity,
                    android.R.layout.simple_spinner_dropdown_item, currencyCodesList)
                setSelection(activityMainViewModel.getSpinnerSelectionIndex())
            }
        })

        activityMainViewModel.lastUpdatedAt.observe(this, Observer {
            val lastUpdatedMinutes = it as Int
            binding.textViewLastUpdatedAt.text = getString(R.string.last_updated, lastUpdatedMinutes)
        })
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        if(binding.editTextInputCurrencyValue.text.isNotEmpty()) {
            val inputValue = binding.editTextInputCurrencyValue.text.toString().toDouble()
            activityMainViewModel.getCalculatedExchangeRates(inputValue, position)
        }
    }

    override fun afterTextChanged(currencyValue: Editable) {
        if(currencyValue.isNotEmpty()) {
            activityMainViewModel.getCalculatedExchangeRates(currencyValue.toString().toDouble(),
                binding.spinnerInputCurrencyCode.selectedItemPosition)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {}

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
}
