package com.example.insider.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.insider.BaseFragment
import com.example.insider.R
import com.example.insider.databinding.FragmentCategoriesBinding
import com.example.insider.models.Data
import com.example.insider.models.Resource
import com.example.insider.models.Status
import com.example.insider.ui.HomeViewModel
import com.example.insider.ui.home.CategoriesAdapter
import com.example.insider.util.extensions.getViewModel
import com.example.insider.util.extensions.glide
import com.example.insider.util.extensions.showShortSnackBar
import javax.inject.Inject

class CategoriesFragment : BaseFragment() {

    companion object {
        private val TAG = CategoriesFragment::class.java.simpleName
    }

    //Global
    @Inject lateinit var factory: ViewModelProvider.Factory
    private lateinit var binding: FragmentCategoriesBinding
    private val viewModel by lazy { requireActivity().getViewModel<HomeViewModel>(factory) }
    private var categoriesAdapter: CategoriesAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_categories, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgBtnBack.setOnClickListener { onBackPressed() }

        setupCategoriesRecycler()

        viewModel.data.observe(viewLifecycleOwner, Observer { bindData(it) })

    }

    private fun setupCategoriesRecycler() {
        categoriesAdapter = CategoriesAdapter(glide())
        categoriesAdapter?.listener = object : CategoriesAdapter.Listener {
            override fun onClick(category: String?) {
                if (category != null) {
                    val action = CategoriesFragmentDirections.navigateCategoriesToCategory(category)
                    findNavController().navigate(action)
                }
            }
        }
        binding.recyclerCategories.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.recyclerCategories.adapter = categoriesAdapter
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

                val categories = data.getCategories()
                if (!categories.isNullOrEmpty()) categoriesAdapter?.swapData(categories)

            }
            Status.ERROR -> showShortSnackBar(resource.message)
        }
    }

}
