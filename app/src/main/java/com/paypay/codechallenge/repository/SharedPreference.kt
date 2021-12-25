package com.paypay.codechallenge.repository

import android.content.SharedPreferences
import com.paypay.codechallenge.database.DatabaseConstants
import javax.inject.Inject

class SharedPreference @Inject constructor(
    private val sharedPreference: SharedPreferences
) {
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