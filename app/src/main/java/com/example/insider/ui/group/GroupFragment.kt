package com.example.insider.ui.group

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.insider.BaseFragment
import com.example.insider.R
import com.example.insider.databinding.FragmentGroupBinding
import com.example.insider.models.Data
import com.example.insider.models.Resource
import com.example.insider.models.Show
import com.example.insider.models.Status
import com.example.insider.ui.HomeViewModel
import com.example.insider.ui.filter.ShowAdapter
import com.example.insider.ui.home.BannerAdapter
import com.example.insider.util.Constants
import com.example.insider.util.CustomTabHelper
import com.example.insider.util.extensions.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import javax.inject.Inject

class GroupFragment : BaseFragment() {

    companion object {
        private val TAG = GroupFragment::class.java.simpleName
    }

    //Global
    @Inject lateinit var factory: ViewModelProvider.Factory
    private lateinit var binding: FragmentGroupBinding
    private val args by navArgs<GroupFragmentArgs>()
    private val viewModel by lazy { requireActivity().getViewModel<HomeViewModel>(factory) }
    private var bannerAdapter: BannerAdapter? = null
    private var showAdapter: ShowAdapter? = null
    private var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_group, container, false)

        binding.txtTitle.text = args.group

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setClickListener()

        setupBottomSheet()

        setupBannerRecycler()

        setupWeekRecycler()

        viewModel.data.observe(viewLifecycleOwner, Observer { bindData(it) })

    }

    private fun setClickListener() {

        binding.imgBtnBack.setOnClickListener { onBackPressed() }

        binding.fabFilterList.setOnClickListener { extendBottomSheet() }

        binding.viewBlur.setOnClickListener { hideBottomSheet() }

        binding.txtBtnReset.setOnClickListener { hideBottomSheet() }

        binding.txtBtnDone.setOnClickListener { hideBottomSheet() }
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetFilters)
        hideBottomSheet()
        bottomSheetBehavior?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                    binding.viewBlur.apply { goneWithFade(parent as ViewGroup) }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
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

    private fun setupWeekRecycler() {
        showAdapter = ShowAdapter()
        showAdapter?.listener = object : ShowAdapter.Listener {
            override fun onClick(show: Show) {
                Log.d(TAG, "TestLog: s:$show")
            }
        }
        binding.recyclerWeek.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerWeek.adapter = showAdapter
    }

    private fun bindData(resource: Resource<Data>) {
        when (resource.status) {
            Status.LOADING -> Unit
            Status.SUCCESS -> {
                val data = resource.data ?: return
                val group = args.group

                val banners = data.getBannersFor(group)
                if (banners.isNotEmpty()) {
                    binding.recyclerBanners.visible()
                    binding.indicatorBanners.visible()
                    bannerAdapter?.swapData(banners)
                }

                val weekShows = data.getShowsFor(group) ?: listOf()
                showAdapter?.swapData(weekShows)

            }
            Status.ERROR -> Log.d(TAG, "TestLog: ${resource.status}: ${resource.message}")
        }
    }

    private fun extendBottomSheet() {
        binding.viewBlur.apply { visibleWithFade(parent as ViewGroup) }
        bottomSheetBehavior?.expand()
    }

    private fun hideBottomSheet() {
        binding.viewBlur.apply { goneWithFade(parent as ViewGroup) }
        bottomSheetBehavior?.hide()
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
