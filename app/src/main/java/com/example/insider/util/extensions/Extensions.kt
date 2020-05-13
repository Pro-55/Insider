package com.example.insider.util.extensions

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

fun FragmentActivity.getDisplayMetrics(): DisplayMetrics {
    val displayMetrics = DisplayMetrics()
    this.windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics
}

fun FragmentActivity.hideKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(findViewById<View>(android.R.id.content)?.windowToken, 0)
}

fun FragmentActivity.showKeyboard(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.showSoftInput(view, 0)
}

inline fun <reified T : ViewModel> FragmentActivity.getViewModel(
    factory: ViewModelProvider.Factory
): T = ViewModelProvider(this, factory).get(T::class.java)
