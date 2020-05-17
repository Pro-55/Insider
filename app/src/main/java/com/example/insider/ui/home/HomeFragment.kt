package com.example.insider.ui.home

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.insider.BaseFragment
import com.example.insider.R
import com.example.insider.databinding.FragmentHomeBinding
import com.example.insider.models.Data
import com.example.insider.models.Event
import com.example.insider.models.Resource
import com.example.insider.models.Status
import com.example.insider.ui.HomeViewModel
import com.example.insider.util.extensions.*
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
    private var categoriesAdapter: CategoriesAdapter? = null
    private var populars: List<Event>? = null
    private var featured: List<Event>? = null

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

        setupCategoriesRecycler()

        binding.txtBtnSeeAll.setOnClickListener { goToCategories() }

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
        popularsAdapter?.listener = object : PopularsAdapter.Listener {
            override fun onClick(_id: String?, isFavorite: Boolean) {
                _id ?: return

                val isPresent = viewModel.favorites.contains(_id)
                if (!isFavorite && !isPresent) viewModel.favorites.add(_id)
                else if (isFavorite) viewModel.favorites.remove(_id)

                updatePopularsList()

            }
        }
        binding.recyclerPopular.layoutManager =
            LinearLayoutManager(requireContext(), HORIZONTAL, false)
        binding.recyclerPopular.adapter = popularsAdapter
        (binding.recyclerPopular.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations =
            false
    }

    private fun setupFeaturedRecycler() {
        featuredAdapter = FeaturedAdapter(glide())
        featuredAdapter?.listener = object : FeaturedAdapter.Listener {
            override fun onClick(_id: String?, isFavorite: Boolean) {
                _id ?: return

                val isPresent = viewModel.favorites.contains(_id)
                if (!isFavorite && !isPresent) viewModel.favorites.add(_id)
                else if (isFavorite) viewModel.favorites.remove(_id)

                updateFeaturedList()

            }
        }
        binding.recyclerFeatured.layoutManager =
            LinearLayoutManager(requireContext(), HORIZONTAL, false)
        binding.recyclerFeatured.adapter = featuredAdapter
        (binding.recyclerFeatured.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations =
            false
    }

    private fun setupCategoriesRecycler() {
        categoriesAdapter = CategoriesAdapter(glide())
        categoriesAdapter?.listener = object : CategoriesAdapter.Listener {
            override fun onClick(category: String?) {
                if (category != null) {
                    val action = HomeFragmentDirections.navigateHomeToCategory(category)
                    findNavController().navigate(action)
                }
            }
        }
        binding.recyclerCategories.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.recyclerCategories.adapter = categoriesAdapter
    }

    private fun bindData(resource: Resource<Data>) {
        when (resource.status) {
            Status.LOADING -> binding.progressHome.visible()
            Status.SUCCESS -> {
                binding.progressHome.gone()

                val data = resource.data

                if (data == null) {
                    binding.imgErrorPlaceholder.visible()
                    binding.txtErrorPlaceholder.visible()
                    showShortSnackBar(resource.message)
                    return
                }

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

                populars = data.getPopularsList()
                if (!populars.isNullOrEmpty()) {
                    binding.txtPopular.visible()
                    binding.recyclerPopular.visible()
                    updatePopularsList()
                }

                featured = data.featured
                if (!featured.isNullOrEmpty()) {
                    binding.txtFeatured.visible()
                    binding.recyclerFeatured.visible()
                    updateFeaturedList()
                }

                val categories = data.getCategoriesShortList()
                if (!categories.isNullOrEmpty()) {
                    binding.txtCategories.visible()
                    binding.txtBtnSeeAll.visible()
                    binding.recyclerCategories.visible()
                    categoriesAdapter?.swapData(categories)
                }

            }
            Status.ERROR -> {
                binding.progressHome.gone()
                binding.imgErrorPlaceholder.visible()
                binding.txtErrorPlaceholder.visible()
                showShortSnackBar(resource.message)
            }
        }
    }

    private fun goToCategories() {
        val action = HomeFragmentDirections.navigateHomeToCategories()
        findNavController().navigate(action)
    }

    private fun updateFeaturedList() {
        val favorites = viewModel.favorites
        val list =
            featured!!.map { e -> if (favorites.contains(e._id)) e.copy(isFavorite = true) else e }
        featuredAdapter?.swapData(list)
    }

    private fun updatePopularsList() {
        populars ?: return
        val favorites = viewModel.favorites
        val list =
            populars!!.map { e -> if (favorites.contains(e._id)) e.copy(isFavorite = true) else e }
        popularsAdapter?.swapData(list)
    }

}