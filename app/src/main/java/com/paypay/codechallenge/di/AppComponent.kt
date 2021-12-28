package com.paypay.codechallenge.di

import com.paypay.codechallenge.PayPayCodeChallenge
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    ActivityBuilderModule::class,
    AppModule::class,
    DataModule::class,
    ViewModelFactoryModule::class
])
interface AppComponent {
    fun inject(application: PayPayCodeChallenge)
}