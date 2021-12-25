package com.paypay.codechallenge.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.paypay.codechallenge.database.DatabaseConstants
import com.paypay.codechallenge.database.ExchangeRatesDatabase
import com.paypay.codechallenge.network.RetrofitApi
import com.paypay.codechallenge.network.RetrofitBuilder
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule constructor(
    private val context: Context
) {
    @Singleton
    @Provides
    fun provideRetrofitApi(retrofitBuilder: RetrofitBuilder): RetrofitApi {
        return retrofitBuilder.retrofit.create(RetrofitApi::class.java)
    }

    @Singleton
    @Provides
    fun provideRoomDatabase() : ExchangeRatesDatabase? {
        return Room.databaseBuilder(context, ExchangeRatesDatabase::class.java,
            DatabaseConstants.DATABASE_NAME).build()
    }

    @Singleton
    @Provides
    fun providesSharedPreference(): SharedPreferences {
        return context.getSharedPreferences(DatabaseConstants.SHARED_PREFERENCE_NAME,
            Context.MODE_PRIVATE)
    }
}