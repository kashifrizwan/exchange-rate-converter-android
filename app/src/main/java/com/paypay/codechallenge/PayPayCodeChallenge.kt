package com.paypay.codechallenge

import android.app.Application
import android.content.Context

class PayPayCodeChallenge : Application() {

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    companion object {
        var context: Context? = null
    }
}