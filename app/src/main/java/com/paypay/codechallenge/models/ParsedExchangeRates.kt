package com.paypay.codechallenge.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class ParsedExchangeRates (

    @PrimaryKey
    @ColumnInfo(name = "currency_code")
    var currencyCode: String,

    @ColumnInfo(name = "rate")
    var exchangeRate: Double

) : Serializable