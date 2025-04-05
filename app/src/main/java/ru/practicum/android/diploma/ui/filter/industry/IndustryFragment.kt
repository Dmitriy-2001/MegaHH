package ru.practicum.android.diploma.ui.filter.industry

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentIndustryBinding
import ru.practicum.android.diploma.domain.filter.models.FilterParam
import ru.practicum.android.diploma.presentation.filter.IndustryScreenState
import ru.practicum.android.diploma.presentation.filter.IndustryViewModel
import ru.practicum.android.diploma.util.gone
import ru.practicum.android.diploma.util.show

class IndustryFragment : Fragment() {

    private var _binding: FragmentIndustryBinding? = null
    private val binding: FragmentIndustryBinding
        get() = requireNotNull(_binding) { "Binding is null" }

    private val viewModel by viewModel<IndustryViewModel>()
    private var adapter: IndustryAdapter? = null

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
        adapter = IndustryAdapter(
            emptyList(),
            selectedIndustry = null
        ) { industry ->
            onIndustrySelected(industry)
            binding.buttonChoice.isVisible = true
        }
        binding.industryList.adapter = adapter
        viewModel.state.observe(viewLifecycleOwner) { state ->
            hidePlaceholders()
            when (state) {
                is IndustryScreenState.Content -> {
                    if (state.data.isEmpty()) {
                        binding.placeholderNotFound.root.show()
                        binding.industryList.gone()
                    } else {
                        showContent(state.data)
                    }
                }

                else -> showError(state)
            }
        }

        setupSearchListeners()
        setupToolbar()

        viewModel.selectedIndustry.observe(viewLifecycleOwner) { selectedIndustry ->
            adapter?.itemsForAll?.let {
                adapter?.updateItems(
                    it,
                    selectedIndustry
                )
            }
        }

        binding.buttonChoice.setOnClickListener {
            viewModel.confirmSelection()
            findNavController().navigateUp()
        }
    }

    private fun hidePlaceholders() {
        binding.placeholderNotFound.root.gone()
        binding.placeholderNoInternet.root.gone()
        binding.placeholderEmptyList.root.gone()
    }

    private fun cleanSearch() {
        if (binding.searchEditText.text.isNotBlank()) {
            binding.searchEditText.text.clear()
            binding.placeholderNotFound.root.gone()
            binding.industryList.show()
        }
    }

    private fun updateSearchIcon(query: String) {
        if (query.isNotBlank()) {
            binding.searchOrClearIcon.setImageResource(R.drawable.ic_clear)
        } else {
            binding.searchOrClearIcon.setImageResource(R.drawable.ic_search)
            hideKeyboard()
        }
    }

    private fun showError(screenState: IndustryScreenState) =
        when (screenState) {
            is IndustryScreenState.NoInternet -> setNoInternetState()
            is IndustryScreenState.Error -> setServerErrorState()
            is IndustryScreenState.NothingFound -> setNothingFoundState()
            else -> {}
        }


    private fun showContent(industries: List<FilterParam>) {
        binding.progressBar.gone()
        binding.industryList.show()
        adapter?.updateItems(industries, viewModel.selectedIndustry.value)
        adapter?.notifyDataSetChanged()
    }

    private fun setServerErrorState() {
        listOf(
            binding.searchContainer,
            binding.progressBar,
            binding.industryList,
            binding.placeholderNotFound.root,
            binding.placeholderNoInternet.root,
        ).gone()
        binding.placeholderEmptyList.root.show()
    }

    private fun setNoInternetState() {
        listOf(
            binding.searchContainer,
            binding.progressBar,
            binding.industryList,
            binding.placeholderEmptyList.root,
            binding.placeholderNotFound.root,
        ).gone()
        binding.placeholderNoInternet.root.show()
    }

    private fun setNothingFoundState() {
        listOf(
            binding.searchContainer,
            binding.progressBar,
            binding.industryList,
            binding.placeholderEmptyList.root,
            binding.placeholderNoInternet.root,
        ).gone()
        binding.placeholderNotFound.root.show()
    }

    private fun onIndustrySelected(industry: FilterParam) {
        viewModel.selectIndustry(industry)
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    private fun setupSearchListeners() {
        updateSearchIcon(binding.searchEditText.text.toString())
        binding.searchEditText.addTextChangedListener { text ->
            val query = text.toString()
            updateSearchIcon(query)
            viewModel.filterIndustries(query)
        }

        binding.searchOrClearIcon.setOnClickListener { cleanSearch() }

        binding.searchEditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                hideKeyboard()
                viewModel.filterIndustries(binding.searchEditText.text.toString())
                true
            } else {
                false
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.toolbar.setOnClickListener { findNavController().navigateUp() }
    }
}
