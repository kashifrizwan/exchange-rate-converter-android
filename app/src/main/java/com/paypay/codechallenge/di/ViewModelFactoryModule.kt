package com.paypay.codechallenge.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.paypay.codechallenge.di.viewmodelfactory.ViewModelFactory
import dagger.Module
import dagger.Provides
import javax.inject.Provider
import javax.inject.Singleton

@Module
class ViewModelFactoryModule {

    @Provides
    @Singleton
    fun providesViewModelFactory(map: Map<Class<out ViewModel>, Provider<ViewModel>>) : ViewModelProvider.Factory {
        return ViewModelFactory(map)
    }
}