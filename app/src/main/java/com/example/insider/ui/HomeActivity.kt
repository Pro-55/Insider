package com.example.insider.ui

import android.os.Bundle
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
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        viewModel
    }

}