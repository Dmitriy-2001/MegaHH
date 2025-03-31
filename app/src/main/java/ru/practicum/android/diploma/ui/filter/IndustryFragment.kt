package ru.practicum.android.diploma.ui.filter

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentIndustryBinding
import ru.practicum.android.diploma.domain.filter.models.FilterParam
import ru.practicum.android.diploma.presentation.search.IndustryScreenState
import ru.practicum.android.diploma.presentation.search.IndustryViewModel
import ru.practicum.android.diploma.util.gone
import ru.practicum.android.diploma.util.show

class IndustryFragment : Fragment() {

    private var _binding: FragmentIndustryBinding? = null
    private val binding: FragmentIndustryBinding
        get() = requireNotNull(_binding) { "Binding is null" }

    private val viewModel by viewModel<IndustryViewModel>()
    private lateinit var adapter: IndustryAdapter
    private var query = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentIndustryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        adapter = IndustryAdapter(
            emptyList(),
            selectedIndustry = null
        ) { industry ->
            onIndustrySelected(industry)
        }
        binding.industryList.adapter = adapter

        binding.searchEditText.addTextChangedListener { text ->
            val query = text.toString()
            updateSearchIcon(text.toString())
            viewModel.filterIndustries(query)
        }

        updateSearchIcon(binding.searchEditText.text.toString())

        binding.searchOrClearIcon.setOnClickListener {
            if (binding.searchEditText.text.isNotBlank()) {
                binding.searchEditText.text.clear()
            }
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is IndustryScreenState.Loading -> showLoading()
                is IndustryScreenState.Content -> showContent(state.data)
                is IndustryScreenState.Error -> showError()
                is IndustryScreenState.NoInternet -> showNoInternet()
                is IndustryScreenState.NothingFound -> showNothingFound()
            }
        }

        viewModel.selectedIndustry.observe(viewLifecycleOwner) { selectedIndustry ->
            adapter.updateItems(adapter.items, selectedIndustry)
        }
    }

    private fun onIndustrySelected(industry: FilterParam) {
        viewModel.selectIndustry(industry)
        findNavController().navigateUp()
    }

    private fun startSearch() {
        hideKeyboard()
        viewModel.filterIndustries(query)
    }

    private fun showLoading() {
        binding.progressBar.show()
    }

    private fun showContent(industries: List<FilterParam>) {
        binding.progressBar.gone()
        adapter.updateItems(industries, viewModel.selectedIndustry.value)
        adapter.notifyDataSetChanged()
    }

    private fun showError() {
        binding.placeholderServerError.root.show()
        binding.progressBar.gone()
    }

    private fun showNoInternet() {
        binding.placeholderNoInternet.root.show()
        binding.progressBar.gone()
    }

    private fun showNothingFound() {
        binding.placeholderEmptyList.root.show()
        binding.progressBar.gone()
    }

    private fun updateSearchIcon(text: String) {
        if (text.isNotBlank()) {
            binding.searchOrClearIcon.setImageResource(R.drawable.ic_clear)
        } else {
            binding.searchOrClearIcon.setImageResource(R.drawable.ic_search)
        }
    }
    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

}



