package ru.practicum.android.diploma.ui.filter

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilterBinding
import ru.practicum.android.diploma.domain.filter.models.FilterParams
import ru.practicum.android.diploma.presentation.filter.FilterScreenState
import ru.practicum.android.diploma.presentation.filter.FilterViewModel

class FilterFragment : Fragment() {

    private var _binding: FragmentFilterBinding? = null
    private val binding: FragmentFilterBinding
        get() = requireNotNull(_binding) { "Binding is null" }

    private val viewModel by viewModel<FilterViewModel>()

    private var isInitialLoad = true
    private var currentSalaryText = ""
    private var isSalaryFocused = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterBinding.inflate(inflater, container, false)
        arguments?.getString(SEARCH_QUERY_ARG)?.let {
            viewModel.setSearchQuery(it)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupClickListeners()
        setupSalaryInput()
        setupObservers()
        viewModel.updateFilterParameters()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupClickListeners() {
        binding.industry.setOnClickListener {
            if (binding.industry.text.toString() != getString(R.string.industry)) {
                clearIndustry()
            } else {
                openIndustry()
            }
        }

        binding.workplace.setOnClickListener {
            if (binding.workplace.text.toString() != getString(R.string.workplace)) {
                clearWorkplace()
            } else {
                openWorkplace()
            }
        }

        binding.applyFilters.setOnClickListener {
            viewModel.saveFilters(
                salary = binding.salaryInput.text?.toString()?.trim(),
                doNotShowWithoutSalary = binding.hideWithoutSalary.isChecked
            )
            navigateBackToSearchWithRefresh()
        }

        binding.resetFilters.setOnClickListener {
            viewModel.resetFilters()
        }

        binding.salaryClear.setOnClickListener {
            binding.salaryInput.text?.clear()
        }

        binding.hideWithoutSalary.setOnCheckedChangeListener { _, _ ->
            checkFiltersChanged()
        }
    }

    private fun setupSalaryInput() {
        binding.salaryInput.setOnFocusChangeListener { _, hasFocus ->
            isSalaryFocused = hasFocus
            updateSalaryHintColor()
            updateClearButtonVisibility()
        }

        binding.salaryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                binding.salaryInput.clearFocus()
                true
            } else {
                false
            }
        }

        binding.salaryInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentSalaryText = s?.toString() ?: ""
                updateClearButtonVisibility()
                updateSalaryHintColor()
                checkFiltersChanged()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateClearButtonVisibility() {
        binding.salaryClear.isVisible = currentSalaryText.isNotEmpty() && isSalaryFocused
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.salaryInput.windowToken, 0)
    }

    private fun updateSalaryHintColor() {
        val colorResId = when {
            isSalaryFocused -> R.color.blue
            currentSalaryText.isNotEmpty() -> R.color.black
            else -> if (isSystemInDarkMode()) R.color.white else R.color.gray
        }
        binding.salaryHint.setTextColor(ContextCompat.getColor(requireContext(), colorResId))
    }

    private fun updateFieldArrows() {
        val hasIndustry = binding.industry.text.toString() != getString(R.string.industry)
        val hasWorkplace = binding.workplace.text.toString() != getString(R.string.workplace)

        binding.industryArrow.setImageResource(
            if (hasIndustry) R.drawable.ic_clear else R.drawable.ic_arrow_forward
        )
        binding.workplaceArrow.setImageResource(
            if (hasWorkplace) R.drawable.ic_clear else R.drawable.ic_arrow_forward
        )

        // Обновляем цвет текста
        binding.industry.setTextColor(ContextCompat.getColor(requireContext(),
            if (hasIndustry) R.color.black else R.color.gray))
        binding.workplace.setTextColor(ContextCompat.getColor(requireContext(),
            if (hasWorkplace) R.color.black else R.color.gray))
    }

    private fun clearIndustry() {
        binding.industry.text = getString(R.string.industry)
        binding.industry.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
        binding.industryArrow.setImageResource(R.drawable.ic_arrow_forward)
        checkFiltersChanged()
    }

    private fun clearWorkplace() {
        binding.workplace.text = getString(R.string.workplace)
        binding.workplace.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
        binding.workplaceArrow.setImageResource(R.drawable.ic_arrow_forward)
        checkFiltersChanged()
    }

    private fun isSystemInDarkMode(): Boolean {
        val nightModeFlags = resources.configuration.uiMode and
            android.content.res.Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES
    }

    private fun setupObservers() {
        viewModel.getFilterScreenState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is FilterScreenState.Content -> {
                    displayFilterContent(state.filterParams)
                    if (isInitialLoad) {
                        currentSalaryText = state.filterParams.salary ?: ""
                        isInitialLoad = false
                        updateSalaryHintColor()
                    }
                    checkFiltersChanged()
                    updateFieldArrows()
                }

                is FilterScreenState.NoFilterSelected -> {
                    resetFilterUI()
                    isInitialLoad = false
                    checkFiltersChanged()
                    updateFieldArrows()
                }
            }
        }
    }

    private fun displayFilterContent(filterParams: FilterParams) {
        binding.industry.text = filterParams.industry?.name ?: getString(R.string.industry)

        filterParams.area?.let { area ->
            binding.workplace.text = area.name
        } ?: filterParams.country?.let { country ->
            binding.workplace.text = country.name
        } ?: run {
            binding.workplace.text = getString(R.string.workplace)
        }

        binding.salaryInput.setText(filterParams.salary ?: "")
        binding.hideWithoutSalary.isChecked = filterParams.doNotShowWithoutSalary ?: false

        val hasActiveFilters = filterParams.industry != null ||
            filterParams.area != null ||
            filterParams.country != null ||
            !filterParams.salary.isNullOrEmpty() ||
            filterParams.doNotShowWithoutSalary == true

        binding.resetFilters.isVisible = hasActiveFilters
    }

    private fun resetFilterUI() {
        binding.industry.text = getString(R.string.industry)
        binding.workplace.text = getString(R.string.workplace)
        binding.salaryInput.text?.clear()
        binding.hideWithoutSalary.isChecked = false
        binding.resetFilters.isVisible = false
        binding.applyFilters.isVisible = false
        updateSalaryHintColor()
        updateFieldArrows()
    }

    private fun checkFiltersChanged() {
        val currentState = viewModel.getFilterScreenState().value
        if (currentState is FilterScreenState.Content) {
            val currentFilters = currentState.filterParams
            val salaryChanged = currentSalaryText != (currentFilters.salary ?: "")
            val checkboxChanged = binding.hideWithoutSalary.isChecked != (currentFilters.doNotShowWithoutSalary ?: false)
            val industryChanged = binding.industry.text.toString() != (currentFilters.industry?.name ?: getString(R.string.industry))
            val workplaceChanged = binding.workplace.text.toString() != when {
                currentFilters.area != null -> currentFilters.area.name
                currentFilters.country != null -> currentFilters.country.name
                else -> getString(R.string.workplace)
            }

            binding.applyFilters.isVisible = salaryChanged || checkboxChanged || industryChanged || workplaceChanged
        } else {
            val hasFilters = currentSalaryText.isNotEmpty() ||
                binding.hideWithoutSalary.isChecked ||
                binding.industry.text.toString() != getString(R.string.industry) ||
                binding.workplace.text.toString() != getString(R.string.workplace)

            binding.applyFilters.isVisible = hasFilters
        }
    }

    private fun navigateBackToSearchWithRefresh() {
        val searchQuery = viewModel.getSearchQuery()
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            SHOULD_REFRESH_SEARCH, true
        )
        findNavController().navigateUp()
    }

    private fun openIndustry() {
        val directions = FilterFragmentDirections.actionFilterFragmentToIndustryFragment()
        findNavController().navigate(directions)
    }

    private fun openWorkplace() {
        val directions = FilterFragmentDirections.actionFilterFragmentToPlaceOfWorkFragment()
        findNavController().navigate(directions)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val SEARCH_QUERY_ARG = "search_query"
        const val SHOULD_REFRESH_SEARCH = "should_refresh_search"
    }
}
