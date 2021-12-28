package com.paypay.codechallenge.di.viewmodelfactory

import androidx.lifecycle.ViewModel
import javax.inject.Inject
import androidx.lifecycle.ViewModelProvider
import javax.inject.Provider

class ViewModelFactory @Inject constructor(
    private val viewModelMap : Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return viewModelMap[modelClass]!!.get() as T
    }
}