package com.paypay.codechallenge.models

data class LiveExchangeRates (
    val success: Boolean,
    val terms: String,
    val privacy: String,
    val timestamp: Int,
    val source: String,
    val quotes: HashMap<String, Double>
)