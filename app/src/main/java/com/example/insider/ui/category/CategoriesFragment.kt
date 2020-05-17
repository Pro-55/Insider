package com.example.insider.ui.category

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.insider.models.SubData
import com.example.insider.ui.HomeViewModel
import com.example.insider.ui.home.CategoriesAdapter
import com.example.insider.util.extensions.*
import javax.inject.Inject

class CategoriesFragment : BaseFragment() {

    companion object {
        private val TAG = CategoriesFragment::class.java.simpleName
    }

    //Global
    @Inject lateinit var factory: ViewModelProvider.Factory
    private lateinit var binding: FragmentCategoriesBinding
    private val viewModel by lazy { requireActivity().getViewModel<HomeViewModel>(factory) }
    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            searchQuery = s?.toString()?.trim() ?: ""
            updateList()
        }
    }
    private var categoriesAdapter: CategoriesAdapter? = null
    private var categories: List<SubData>? = null
    private var searchQuery = ""

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

        setListeners()

        setupCategoriesRecycler()

        viewModel.data.observe(viewLifecycleOwner, Observer { bindData(it) })

    }

    private fun setListeners() {

        binding.imgBtnBack.setOnClickListener { onBackPressed() }

        binding.imgBtnSearch.setOnClickListener { enableSearch() }

        binding.imgBtnCancel.setOnClickListener { disableSearch() }

        binding.editSearch.addTextChangedListener(textWatcher)

        binding.editSearch.setOnEditorActionListener { _, _, _ ->
            clearFocus()
            hideKeyboard()
            true
        }

    }

    private fun setupCategoriesRecycler() {
        categoriesAdapter = CategoriesAdapter(glide())
        categoriesAdapter?.listener = object : CategoriesAdapter.Listener {
            override fun onClick(category: String?) {
                if (category != null) {
                    disableSearch()
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

                categories = data.getCategories()
                if (!categories.isNullOrEmpty()) updateList()

            }
            Status.ERROR -> showShortSnackBar(resource.message)
        }
    }

    private fun updateList() {
        val list = categories?.mapNotNull { e ->
            if (e.name?.contains(searchQuery, true) == true) e else null
        } ?: return
        categoriesAdapter?.swapData(list)
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
        updateList()
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
