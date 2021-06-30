package com.paypay.codechallenge.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paypay.codechallenge.models.ParsedExchangeRates
import com.paypay.codechallenge.databinding.RowCurrencyViewBinding

class ExchangeRatesAdapter (private val currencyList: List<ParsedExchangeRates>) : RecyclerView.Adapter<ExchangeRatesAdapter.CurrencyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val currencyItemBinding = RowCurrencyViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CurrencyViewHolder(currencyItemBinding)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) = holder.bind(currencyList[position])

    override fun getItemCount(): Int = currencyList.size

    inner class CurrencyViewHolder(private val binding: RowCurrencyViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(parsedExchangeRates: ParsedExchangeRates) {
            binding.parsedExchangeRates = parsedExchangeRates
        }
    }
}