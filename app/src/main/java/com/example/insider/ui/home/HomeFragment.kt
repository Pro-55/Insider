package com.example.insider.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.insider.BaseFragment
import com.example.insider.R
import com.example.insider.databinding.FragmentHomeBinding
import com.example.insider.models.Data
import com.example.insider.models.Resource
import com.example.insider.models.Status
import com.example.insider.ui.HomeViewModel
import com.example.insider.util.Constants
import com.example.insider.util.CustomTabHelper
import com.example.insider.util.extensions.getViewModel
import com.example.insider.util.extensions.glide
import com.example.insider.util.extensions.showShortSnackBar
import javax.inject.Inject

class HomeFragment : BaseFragment() {

    companion object {
        private val TAG = HomeFragment::class.java.simpleName
    }

    //Global
    @Inject lateinit var factory: ViewModelProvider.Factory
    private lateinit var binding: FragmentHomeBinding
    private val viewModel by lazy { requireActivity().getViewModel<HomeViewModel>(factory) }
    private var bannerAdapter: BannerAdapter? = null
    private var groupAdapter: GroupAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBannerRecycler()

        setupGroupRecycler()

        viewModel.data.observe(viewLifecycleOwner, Observer { bindData(it) })

    }

    private fun setupBannerRecycler() {
        bannerAdapter = BannerAdapter(glide())
        bannerAdapter?.listener = object : BannerAdapter.Listener {
            override fun onClick(link: String?) {
                val uri = Uri.parse(link)
                openLink(uri)
            }
        }
        binding.recyclerHomeBanners.layoutManager =
            LinearLayoutManager(requireContext(), HORIZONTAL, false)
        binding.recyclerHomeBanners.adapter = bannerAdapter
        PagerSnapHelper().attachToRecyclerView(binding.recyclerHomeBanners)
        binding.indicatorBanners.attachToRecyclerView(binding.recyclerHomeBanners)
    }

    private fun setupGroupRecycler() {
        groupAdapter = GroupAdapter()
        binding.recyclerHomeGroups.layoutManager =
            LinearLayoutManager(requireContext(), HORIZONTAL, false)
        binding.recyclerHomeGroups.adapter = groupAdapter
    }

    private fun bindData(resource: Resource<Data>) {
        when (resource.status) {
            Status.LOADING -> Unit
            Status.SUCCESS -> {
                val data = resource.data ?: return

                val banners = data.banners
                bannerAdapter?.swapData(banners)

                val groups = data.groups
                groupAdapter?.swapData(groups)
            }
            Status.ERROR -> Log.d(TAG, "TestLog: ${resource.status}: ${resource.message}")
        }
    }

    private fun openLink(uri: Uri) {
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

}