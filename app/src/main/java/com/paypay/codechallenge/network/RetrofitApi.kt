package com.paypay.codechallenge.network

import com.paypay.codechallenge.models.LiveExchangeRates
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitApi {

    @GET(Url.ENDPOINT_EXCHANGE_RATES)
    suspend fun getAllExchangeRates(@Query(NetworkConstants.ACCESS_KEY) accessKey: String): LiveExchangeRates
}