package com.example.insider.util.extensions

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

inline fun <reified T : ViewModel> FragmentActivity.getViewModel(
    factory: ViewModelProvider.Factory
): T = ViewModelProvider(this, factory).get(T::class.java)

fun String.deQuoteString(): String {
    val regex = "\""
    return removePrefix(regex).removeSuffix(regex).trim()
}