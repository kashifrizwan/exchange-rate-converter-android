package com.paypay.codechallenge.repository

import android.content.Context
import com.paypay.codechallenge.PayPayCodeChallenge
import com.paypay.codechallenge.database.DatabaseConstants

class SharedPreference {
    companion object{
        private val sharedPreference = PayPayCodeChallenge.context!!.getSharedPreferences(
            DatabaseConstants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)

        fun saveLastUpdatedAt(timeInMillis: Long) {
            with (sharedPreference.edit()) {
                putLong(DatabaseConstants.LAST_UPDATED_AT, timeInMillis)
                apply()
            }
        }

        fun getLastUpdatedAt(): Long {
            return sharedPreference.getLong(DatabaseConstants.LAST_UPDATED_AT, 0)
        }
    }
}