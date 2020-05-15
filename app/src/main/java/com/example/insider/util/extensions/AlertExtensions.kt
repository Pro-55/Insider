package com.example.insider.util.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.insider.util.Constants
import com.google.android.material.snackbar.Snackbar

fun Fragment.showShortSnackBar(message: String?) = requireActivity().showShortSnackBar(message)

fun FragmentActivity.showShortSnackBar(message: String?) {
    Snackbar.make(
        findViewById(android.R.id.content),
        message ?: Constants.REQUEST_FAILED_MESSAGE,
        Snackbar.LENGTH_SHORT
    ).show()
}

fun Fragment.showLongSnackBar(message: String?) = requireActivity().showLongSnackBar(message)

fun FragmentActivity.showLongSnackBar(message: String?) {
    Snackbar.make(
        findViewById(android.R.id.content),
        message ?: Constants.REQUEST_FAILED_MESSAGE,
        Snackbar.LENGTH_LONG
    ).show()
}