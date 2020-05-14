package com.example.insider.ui

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.insider.R
import com.example.insider.databinding.ActivityHomeBinding
import com.example.insider.util.extensions.getViewModel
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class HomeActivity : DaggerAppCompatActivity() {

    companion object {
        private val TAG = HomeActivity::class.java.simpleName
    }

    //Global
    @Inject lateinit var factory: ViewModelProvider.Factory
    private lateinit var binding: ActivityHomeBinding
    private val viewModel by lazy { getViewModel<HomeViewModel>(factory) }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val colorBackground = resources.getColor(R.color.color_background)
            window.statusBarColor = colorBackground
            window.navigationBarColor = colorBackground
            val isNightMode =
                when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_YES -> true
                    else -> false
                }
            if (!isNightMode) {
                window.decorView.systemUiVisibility =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                    else View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        viewModel.fetchData()
    }

}