package com.paypay.codechallenge

import android.app.Application
import android.content.Context
import com.paypay.codechallenge.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class PayPayCodeChallenge : Application(), HasAndroidInjector {

    @Inject
    lateinit var mInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()

        DaggerAppComponent.builder().build().inject(this);
        context = applicationContext
    }

    companion object {
        var context: Context? = null
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return mInjector
    }
}