package com.paypay.codechallenge.di

import androidx.lifecycle.ViewModel
import com.paypay.codechallenge.annotations.ViewModelKey
import com.paypay.codechallenge.viewmodel.MainActivityViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    abstract fun bindMainActivityViewModel(mainActivityViewModel: MainActivityViewModel) : ViewModel
}