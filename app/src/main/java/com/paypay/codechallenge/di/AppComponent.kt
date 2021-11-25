package com.paypay.codechallenge.di

import com.paypay.codechallenge.PayPayCodeChallenge
import dagger.Component
import dagger.android.AndroidInjectionModule

@Component(modules = [
    AndroidInjectionModule::class,
    MainActivityModule::class
])
interface AppComponent {
    fun inject(application: PayPayCodeChallenge)
}