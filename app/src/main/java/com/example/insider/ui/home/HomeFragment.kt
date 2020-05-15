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
import androidx.navigation.fragment.findNavController
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
import com.example.insider.util.extensions.visible
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
    private var popularsAdapter: PopularsAdapter? = null
    private var featuredAdapter: FeaturedAdapter? = null

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

        setupPopularRecycler()

        setupFeaturedRecycler()

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
        binding.recyclerBanners.layoutManager =
            LinearLayoutManager(requireContext(), HORIZONTAL, false)
        binding.recyclerBanners.adapter = bannerAdapter
        PagerSnapHelper().attachToRecyclerView(binding.recyclerBanners)
        binding.indicatorBanners.attachToRecyclerView(binding.recyclerBanners)
    }

    private fun setupGroupRecycler() {
        groupAdapter = GroupAdapter()
        groupAdapter?.listener = object : GroupAdapter.Listener {
            override fun onClick(group: String?) {
                if (group != null) {
                    val action = HomeFragmentDirections.navigateHomeToGroup(group)
                    findNavController().navigate(action)
                }
            }
        }
        binding.recyclerGroups.layoutManager =
            LinearLayoutManager(requireContext(), HORIZONTAL, false)
        binding.recyclerGroups.adapter = groupAdapter
    }

    private fun setupPopularRecycler() {
        popularsAdapter = PopularsAdapter(glide())
        binding.recyclerPopular.layoutManager =
            LinearLayoutManager(requireContext(), HORIZONTAL, false)
        binding.recyclerPopular.adapter = popularsAdapter
    }

    private fun setupFeaturedRecycler() {
        featuredAdapter = FeaturedAdapter(glide())
        binding.recyclerFeatured.layoutManager =
            LinearLayoutManager(requireContext(), HORIZONTAL, false)
        binding.recyclerFeatured.adapter = featuredAdapter
    }

    private fun bindData(resource: Resource<Data>) {
        when (resource.status) {
            Status.LOADING -> Unit
            Status.SUCCESS -> {
                val data = resource.data ?: return

                val banners = data.getBannersHome()
                if (!banners.isNullOrEmpty()) {
                    binding.recyclerBanners.visible()
                    binding.indicatorBanners.visible()
                    bannerAdapter?.swapData(banners)
                }

                val groups = data.groups
                if (!groups.isNullOrEmpty()) {
                    binding.txtGroups.visible()
                    binding.recyclerGroups.visible()
                    groupAdapter?.swapData(groups)
                }

                val populars = data.getPopularsList()
                if (!populars.isNullOrEmpty()) {
                    binding.txtPopular.visible()
                    binding.recyclerPopular.visible()
                    popularsAdapter?.swapData(populars)
                }

                val featured = data.featured
                if (!featured.isNullOrEmpty()) {
                    binding.txtFeatured.visible()
                    binding.recyclerFeatured.visible()
                    featuredAdapter?.swapData(featured)
                }
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