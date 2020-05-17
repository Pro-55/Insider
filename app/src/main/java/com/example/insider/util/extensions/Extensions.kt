package com.example.insider.util.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.insider.models.Event
import com.example.insider.util.Constants
import com.example.insider.util.CustomTabHelper

fun Fragment.openLink(uri: Uri) {
    try {
        val chromeTabs = CustomTabsIntent.Builder()
            .addDefaultShareMenuItem()
            .setShowTitle(true)
            .build()

        val chromePackageName = CustomTabHelper.getPackageNameToUse(requireContext(), uri)
        if (chromePackageName == null) {
            // Chrome not installed
            val browserIntent = Intent(Intent.ACTION_VIEW, uri)
            if (browserIntent.resolveActivity(requireContext().packageManager) != null)
                startActivity(browserIntent)
        } else {
            chromeTabs.intent.setPackage(chromePackageName)
            chromeTabs.launchUrl(requireContext(), uri)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        showShortSnackBar(Constants.REQUEST_FAILED_MESSAGE)
    }
}

fun FragmentActivity.clearFocus() {
    currentFocus?.clearFocus()
}

fun FragmentActivity.hideKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(findViewById<View>(android.R.id.content)?.windowToken, 0)
}

fun List<Event>.sortAsc(key: String?): List<Event> =
    if (key == "min_price") sortedBy { it.minPrice } else sortedBy { it.startTime }

fun List<Event>.sortDese(key: String?): List<Event> =
    if (key == "min_price") sortedByDescending { it.minPrice } else sortedByDescending { it.startTime }

inline fun <reified T : ViewModel> FragmentActivity.getViewModel(
    factory: ViewModelProvider.Factory
): T = ViewModelProvider(this, factory).get(T::class.java)

fun String.deQuoteString(): String {
    val regex = "\""
    return removePrefix(regex).removeSuffix(regex).trim()
}