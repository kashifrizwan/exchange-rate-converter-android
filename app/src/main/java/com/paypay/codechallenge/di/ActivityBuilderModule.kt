package com.paypay.codechallenge.di

import com.paypay.codechallenge.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    abstract fun contributeMainActivityInjector(): MainActivity
}