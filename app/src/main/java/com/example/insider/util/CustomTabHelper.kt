package com.example.insider.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.text.TextUtils
import androidx.browser.customtabs.CustomTabsService

object CustomTabHelper {

    private var packageNameToUse: String? = null
    private const val STABLE_PACKAGE = "com.android.chrome"
    private const val BETA_PACKAGE = "com.chrome.beta"
    private const val DEV_PACKAGE = "com.chrome.dev"
    private const val LOCAL_PACKAGE = "com.google.android.apps.chrome"

    fun getPackageNameToUse(context: Context, uri: Uri): String? {

        packageNameToUse?.let { return it }

        val pm = context.packageManager

        val activityIntent = Intent(Intent.ACTION_VIEW, uri)
        val defaultViewHandlerInfo = pm.resolveActivity(activityIntent, 0)
        var defaultViewHandlerPackageName: String? = null

        defaultViewHandlerInfo?.let { defaultViewHandlerPackageName = it.activityInfo.packageName }

        val resolvedActivityList = pm.queryIntentActivities(activityIntent, 0)
        val supportedPackages = ArrayList<String>()
        for (info in resolvedActivityList) {
            val serviceIntent = Intent()
            serviceIntent.action = CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION
            serviceIntent.setPackage(info.activityInfo.packageName)

            pm.resolveService(serviceIntent, 0)?.let {
                supportedPackages.add(info.activityInfo.packageName)
            }
        }

        when {
            supportedPackages.isEmpty() -> packageNameToUse = null

            supportedPackages.size == 1 -> packageNameToUse = supportedPackages[0]

            !TextUtils.isEmpty(defaultViewHandlerPackageName)
                    && !hasSpecializedHandlerIntents(context, activityIntent)
                    && supportedPackages.contains(defaultViewHandlerPackageName)
            -> packageNameToUse = defaultViewHandlerPackageName

            supportedPackages.contains(STABLE_PACKAGE) -> packageNameToUse = STABLE_PACKAGE

            supportedPackages.contains(BETA_PACKAGE) -> packageNameToUse = BETA_PACKAGE

            supportedPackages.contains(DEV_PACKAGE) -> packageNameToUse = DEV_PACKAGE

            supportedPackages.contains(LOCAL_PACKAGE) -> packageNameToUse = LOCAL_PACKAGE
        }
        return packageNameToUse
    }

    private fun hasSpecializedHandlerIntents(context: Context, intent: Intent): Boolean {
        try {
            val pm = context.packageManager
            val handlers = pm.queryIntentActivities(intent, PackageManager.GET_RESOLVED_FILTER)
            if (handlers.isNullOrEmpty()) {
                return false
            }
            for (resolveInfo in handlers) {
                val filter = resolveInfo.filter ?: continue
                if (filter.countDataAuthorities() == 0 || filter.countDataPaths() == 0) continue
                if (resolveInfo.activityInfo == null) continue
                return true
            }
        } catch (e: RuntimeException) {
        }
        return false
    }
}