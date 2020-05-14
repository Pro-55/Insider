package com.example.insider.util.extensions

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar

fun Fragment.showShortSnackBar(message: String?) = requireActivity().showShortSnackBar(message)

fun FragmentActivity.showShortSnackBar(message: String?) {
    Snackbar.make(
        findViewById(android.R.id.content),
        message ?: "Something went wrong!",
        Snackbar.LENGTH_SHORT
    ).show()
}

fun Fragment.showLongSnackBar(message: String?) = requireActivity().showLongSnackBar(message)

fun FragmentActivity.showLongSnackBar(message: String?) {
    Snackbar.make(
        findViewById(android.R.id.content),
        message ?: "Something went wrong!",
        Snackbar.LENGTH_LONG
    ).show()
}

fun Fragment.showShortToast(message: String?) = requireActivity().showShortToast(message)

fun FragmentActivity.showShortToast(message: String?) =
    Toast.makeText(this, message ?: "Something went wrong!", Toast.LENGTH_SHORT).show()

fun Fragment.showLongToast(message: String?) = requireActivity().showLongToast(message)

fun FragmentActivity.showLongToast(message: String?) =
    Toast.makeText(this, message ?: "Something went wrong!", Toast.LENGTH_LONG).show()