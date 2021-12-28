package com.paypay.codechallenge.di

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.room.Room
import com.paypay.codechallenge.database.DatabaseConstants
import com.paypay.codechallenge.database.ExchangeRatesDatabase
import com.paypay.codechallenge.network.RetrofitApi
import com.paypay.codechallenge.network.RetrofitBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideRetrofitApi(retrofitBuilder: RetrofitBuilder): RetrofitApi {
        Log.d("ExchangeRateApp", "Retrofit API Interface Created")
        return retrofitBuilder.retrofit.create(RetrofitApi::class.java)
    }

    @Provides
    fun provideRoomDatabase(@ApplicationContext context: Context) : ExchangeRatesDatabase {
        Log.d("ExchangeRateApp", "Room Database Instance Created")
        return Room.databaseBuilder(context, ExchangeRatesDatabase::class.java,
            DatabaseConstants.DATABASE_NAME).build()
    }

    @Provides
    fun providesSharedPreference(@ApplicationContext context: Context): SharedPreferences {
        Log.d("ExchangeRateApp", "User Preference Created")
        return context.getSharedPreferences(DatabaseConstants.SHARED_PREFERENCE_NAME,
            Context.MODE_PRIVATE)
    }
}