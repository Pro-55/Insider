package com.example.insider.ui.group

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.insider.BaseFragment
import com.example.insider.R
import com.example.insider.databinding.FragmentGroupBinding
import com.example.insider.models.*
import com.example.insider.ui.HomeViewModel
import com.example.insider.ui.filter.ShowAdapter
import com.example.insider.ui.home.BannerAdapter
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
    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            searchQuery = s?.toString()?.trim() ?: ""
            filterList()
        }
    }
    private var bannerAdapter: BannerAdapter? = null
    private var showAdapter: ShowAdapter? = null
    private var eventAdapter: EventAdapter? = null
    private var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>? = null
    private var events: List<Event>? = null
    private var sorts: List<Sort>? = null
    private var filter = Pair<Int, Show?>(0, null)
    private var searchQuery = ""

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

        setListeners()

        setupBottomSheet()

        setupBannerRecycler()

        setupEventsRecycler()

        setupSortSpinner()

        setupWeekRecycler()

        viewModel.data.observe(viewLifecycleOwner, Observer { bindData(it) })

    }

    private fun setListeners() {

        binding.imgBtnBack.setOnClickListener { onBackPressed() }

        binding.imgBtnSearch.setOnClickListener { enableSearch() }

        binding.imgBtnCancel.setOnClickListener { disableSearch() }

        binding.fabFilterList.setOnClickListener { extendBottomSheet() }

        binding.viewBlur.setOnClickListener { hideBottomSheet() }

        binding.txtBtnReset.setOnClickListener { resetFilters() }

        binding.txtBtnDone.setOnClickListener { hideBottomSheet() }

        binding.editSearch.addTextChangedListener(textWatcher)

        binding.editSearch.setOnEditorActionListener { _, _, _ ->
            clearFocus()
            hideKeyboard()
            filterList()
            true
        }

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
            override fun onClick(_id: String?, isFavorite: Boolean) {
                _id ?: return

                val isPresent = viewModel.favorites.contains(_id)
                if (!isFavorite && !isPresent) viewModel.favorites.add(_id)
                else if (isFavorite) viewModel.favorites.remove(_id)

                filterList()

            }
        }
        binding.recyclerList.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerList.adapter = eventAdapter
        (binding.recyclerList.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations =
            false
    }

    private fun bindData(resource: Resource<Data>) {
        when (resource.status) {
            Status.LOADING -> Unit
            Status.SUCCESS -> {
                val data = resource.data

                if (data == null) {
                    showShortSnackBar(resource.message)
                    return
                }

                val group = args.group

                val banners = data.getBannersFor(group)
                if (!banners.isNullOrEmpty()) {
                    binding.recyclerBanners.visible()
                    if (banners.size > 1) binding.indicatorBanners.visible()
                    binding.viewSpacing.visible()
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

                if (!sorts.isNullOrEmpty() || !shows.isNullOrEmpty()) binding.fabFilterList.visible()

            }
            Status.ERROR -> showShortSnackBar(resource.message)
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

        val favorites = viewModel.favorites

        val sortedList = when (sort?.type) {
            "asc" -> filteredList.sortAsc(sort.key)
            "desc" -> filteredList.sortDese(sort.key)
            else -> filteredList
        }
            .map { e -> if (favorites.contains(e._id)) e.copy(isFavorite = true) else e }
            .mapNotNull { e -> if (e.name?.contains(searchQuery, true) == true) e else null }

        eventAdapter?.swapData(sortedList)
    }

    private fun enableSearch() {
        binding.layoutToolBar.gone()
        binding.layoutSearch.visible()
    }

    private fun disableSearch() {
        binding.layoutSearch.gone()
        binding.layoutToolBar.visible()
        searchQuery = ""
        clearFocus()
        hideKeyboard()
        filterList()
    }

    private fun clearFocus() {
        requireActivity().clearFocus()
    }

    private fun hideKeyboard() {
        requireActivity().hideKeyboard()
    }

    override fun onDestroyView() {
        binding.editSearch.removeTextChangedListener(textWatcher)
        super.onDestroyView()
    }

}