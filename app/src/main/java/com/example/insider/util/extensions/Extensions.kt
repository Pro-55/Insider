package com.example.insider.util.extensions

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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

inline fun <reified T : ViewModel> FragmentActivity.getViewModel(
    factory: ViewModelProvider.Factory
): T = ViewModelProvider(this, factory).get(T::class.java)

fun String.deQuoteString(): String {
    val regex = "\""
    return removePrefix(regex).removeSuffix(regex).trim()
}