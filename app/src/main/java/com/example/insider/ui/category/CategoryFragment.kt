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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.insider.BaseFragment
import com.example.insider.R
import com.example.insider.databinding.FragmentCategoryBinding
import com.example.insider.models.Data
import com.example.insider.models.Event
import com.example.insider.models.Resource
import com.example.insider.models.Status
import com.example.insider.ui.HomeViewModel
import com.example.insider.ui.group.EventAdapter
import com.example.insider.util.extensions.*
import javax.inject.Inject

class CategoryFragment : BaseFragment() {

    companion object {
        private val TAG = CategoryFragment::class.java.simpleName
    }

    //Global
    @Inject lateinit var factory: ViewModelProvider.Factory
    private lateinit var binding: FragmentCategoryBinding
    private val args by navArgs<CategoryFragmentArgs>()
    private val viewModel by lazy { requireActivity().getViewModel<HomeViewModel>(factory) }
    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            searchQuery = s?.toString()?.trim() ?: ""
            updateList()
        }
    }
    private var eventAdapter: EventAdapter? = null
    private var events: List<Event>? = null
    private var searchQuery = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_category, container, false)

        binding.txtTitle.text = args.category

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()

        setupEventsRecycler()

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

    private fun setupEventsRecycler() {
        eventAdapter = EventAdapter(glide())
        eventAdapter?.listener = object : EventAdapter.Listener {
            override fun onClick(_id: String?, isFavorite: Boolean) {
                _id ?: return

                val isPresent = viewModel.favorites.contains(_id)
                if (!isFavorite && !isPresent) viewModel.favorites.add(_id)
                else if (isFavorite) viewModel.favorites.remove(_id)

                updateList()

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

                val category = args.category

                events = data.getCategorisedListFor(category)
                if (!events.isNullOrEmpty()) updateList()

            }
            Status.ERROR -> showShortSnackBar(resource.message)
        }
    }

    private fun updateList() {
        events ?: return
        val favorites = viewModel.favorites
        val list =
            events!!.map { e -> if (favorites.contains(e._id)) e.copy(isFavorite = true) else e }
                .mapNotNull { e -> if (e.name?.contains(searchQuery, true) == true) e else null }
        eventAdapter?.swapData(list)
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
