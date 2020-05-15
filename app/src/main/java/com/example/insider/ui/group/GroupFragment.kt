package com.example.insider.ui.group

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import com.example.insider.models.*
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
    private var eventAdapter: EventAdapter? = null
    private var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>? = null
    private var events: List<Event>? = null
    private var sorts: List<Sort>? = null
    private var filter = Pair<Int, Show?>(0, null)

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

        setupEventsRecycler()

        setupSortSpinner()

        setupWeekRecycler()

        viewModel.data.observe(viewLifecycleOwner, Observer { bindData(it) })

    }

    private fun setClickListener() {

        binding.imgBtnBack.setOnClickListener { onBackPressed() }

        binding.fabFilterList.setOnClickListener { extendBottomSheet() }

        binding.viewBlur.setOnClickListener { hideBottomSheet() }

        binding.txtBtnReset.setOnClickListener { resetFilters() }

        binding.txtBtnDone.setOnClickListener { hideBottomSheet() }

    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetFilters)
        hideBottomSheet()
        bottomSheetBehavior?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN)
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

    private fun setupSortSpinner() {
        binding.spinnerSorts.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, index: Int, id: Long) {
                filter = filter.copy(first = index)
                filterList()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupWeekRecycler() {
        showAdapter = ShowAdapter()
        showAdapter?.listener = object : ShowAdapter.Listener {
            override fun onClick(show: Show) {
                filter = filter.copy(second = show)
                filterList()
            }
        }
        binding.recyclerShows.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerShows.adapter = showAdapter
    }

    private fun setupEventsRecycler() {
        eventAdapter = EventAdapter(glide())
        eventAdapter?.listener = object : EventAdapter.Listener {
            override fun onClick(event: Event) {
                Log.d(TAG, "TestLog: e:$event")
            }
        }
        binding.recyclerList.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerList.adapter = eventAdapter
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

                events = data.getGroupedListFor(group)
                if (!events.isNullOrEmpty()) filterList()

                sorts = data.getSortsFor(group)
                if (!sorts.isNullOrEmpty()) {
                    binding.txtSorts.visible()
                    binding.spinnerSorts.visible()
                    val items = sorts!!.map { s -> s.display }
                    binding.spinnerSorts.adapter =
                        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
                            .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
                }

                val shows = data.getShowsFor(group)
                if (!shows.isNullOrEmpty()) {
                    binding.txtShows.visible()
                    binding.recyclerShows.visible()
                    showAdapter?.swapData(shows)
                }

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

    private fun resetFilters() {
        filter = Pair(0, null)
        showAdapter?.resetSelection()
        val count = binding.spinnerSorts.adapter?.count ?: -1
        if (count <= 0) filterList() else binding.spinnerSorts.setSelection(0)
    }

    private fun filterList() {
        if (events.isNullOrEmpty() || eventAdapter == null) return
        val sort = if (!sorts.isNullOrEmpty()) sorts?.get(filter.first) else null
        val show = filter.second
        val filteredList = if (show != null)
            events!!.mapNotNull { e -> if (e.applicableFilters?.contains(show.key) == true) e else null }
        else
            events!!

        val sortedList = when (sort?.type) {
            "asc" -> sortAsc(sort.key, filteredList)
            "desc" -> sortDese(sort.key, filteredList)
            else -> filteredList
        }
        eventAdapter?.swapData(sortedList)
    }

    private fun sortAsc(key: String, filteredList: List<Event>): List<Event> =
        if (key == "min_price") filteredList.sortedBy { it.minPrice } else filteredList.sortedBy { it.startTime }

    private fun sortDese(key: String, filteredList: List<Event>): List<Event> =
        if (key == "min_price") filteredList.sortedByDescending { it.minPrice } else filteredList.sortedByDescending { it.startTime }

}