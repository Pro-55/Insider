package com.example.insider.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.insider.BaseFragment
import com.example.insider.R
import com.example.insider.databinding.FragmentCategoryBinding
import com.example.insider.models.Data
import com.example.insider.models.Resource
import com.example.insider.models.Status
import com.example.insider.ui.HomeViewModel
import com.example.insider.ui.group.EventAdapter
import com.example.insider.util.extensions.getViewModel
import com.example.insider.util.extensions.glide
import com.example.insider.util.extensions.showShortSnackBar
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
    private var eventAdapter: EventAdapter? = null

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

        binding.imgBtnBack.setOnClickListener { onBackPressed() }

        setupEventsRecycler()

        viewModel.data.observe(viewLifecycleOwner, Observer { bindData(it) })

    }

    private fun setupEventsRecycler() {
        eventAdapter = EventAdapter(glide())
        binding.recyclerList.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerList.adapter = eventAdapter
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

                val events = data.getCategorisedListFor(category)
                if (!events.isNullOrEmpty()) eventAdapter?.swapData(events)

            }
            Status.ERROR -> showShortSnackBar(resource.message)
        }
    }

}
