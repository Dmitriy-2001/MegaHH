package ru.practicum.android.diploma.ui.filter.region

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentRegionBinding
import ru.practicum.android.diploma.presentation.filter.RegionScreenState
import ru.practicum.android.diploma.presentation.filter.RegionViewModel
import ru.practicum.android.diploma.util.gone
import ru.practicum.android.diploma.util.show

class RegionFragment : Fragment() {
    private var _binding: FragmentRegionBinding? = null
    private val binding: FragmentRegionBinding
        get() = requireNotNull(_binding) { "Binding is null" }

    private val viewModel: RegionViewModel by viewModel()

    private var regionAdapter: RegionAdapter? = null

    private val errorPlaceholders by lazy {
        listOf(
            binding.placeholderRegionEmpty.root,
            binding.placeholderNoRegion.root,
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRegionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        regionAdapter = RegionAdapter(emptyList()) { region ->
            viewModel.saveFilterParams(region)
            findNavController().navigateUp()
        }

        binding.recyclerViewRegions.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewRegions.adapter = regionAdapter

        viewModel.getScreenState().observe(viewLifecycleOwner) { screenState ->
            errorPlaceholders.gone()

            when (screenState) {
                is RegionScreenState.Content -> {
                    if (screenState.data.isEmpty()) {
                        binding.placeholderNoRegion.root.show()
                        binding.recyclerViewRegions.gone()
                    } else {
                        regionAdapter?.updateRegion(screenState.data)
                        binding.recyclerViewRegions.show()
                    }
                }

                else -> showError(screenState)
            }
        }

        binding.searchEditText.addTextChangedListener { text ->
            val query = text.toString()
            updateSearchIcon(text.toString())
            viewModel.loadRegions(query)
        }

        updateSearchIcon(binding.searchEditText.text.toString())

        binding.searchOrClearIcon.setOnClickListener {
            if (binding.searchEditText.text.isNotBlank()) {
                binding.searchEditText.text.clear()
            }
        }

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard()
                true
            } else {
                false
            }
        }

        binding.toolbar.setOnClickListener { findNavController().navigateUp() }
    }

    private fun updateSearchIcon(query: String) {
        if (query.isNotBlank()) {
            binding.searchOrClearIcon.setImageResource(R.drawable.ic_clear)
        } else {
            binding.searchOrClearIcon.setImageResource(R.drawable.ic_search)
        }
    }

    private fun showError(screenState: RegionScreenState) {
        binding.recyclerViewRegions.gone()

        when (screenState) {
            is RegionScreenState.Error -> binding.placeholderRegionEmpty.root.show()
            is RegionScreenState.NoInternet -> binding.placeholderRegionEmpty.root.show()
            is RegionScreenState.NothingFound -> binding.placeholderNoRegion.root.show()
            else -> {}
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusedView = activity?.currentFocus
        currentFocusedView?.let {
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
