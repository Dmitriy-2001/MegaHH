package ru.practicum.android.diploma.ui.filter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout.END_ICON_CLEAR_TEXT
import com.google.android.material.textfield.TextInputLayout.END_ICON_NONE
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

    private var initSalary: String = ""
    private var initCheckboxValue = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterBinding.inflate(inflater, container, false)
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
        binding.industryClickOverlay.setOnClickListener {
            openIndustry()
        }

        binding.industryIcon.setOnClickListener {
            if (binding.industryEditText.text.toString().isNotEmpty()) {
                clearIndustry()
            } else {
                openIndustry()
            }
        }

        binding.workplaceClickOverlay.setOnClickListener {
            openWorkplace()
        }

        binding.workplaceIcon.setOnClickListener {
            if (binding.workplaceEditText.text.toString().isNotEmpty()) {
                clearWorkplace()
            } else {
                openWorkplace()
            }
        }

        binding.applyFilters.setOnClickListener {
            navigateBackToSearchWithRefresh()
        }

        binding.resetFilters.setOnClickListener {
            viewModel.resetFilters()
        }

        binding.hideWithoutSalary.setOnClickListener {
            binding.hideWithoutSalary.toggle()
            if (initCheckboxValue != binding.hideWithoutSalary.isChecked) {
                viewModel.saveDoNotShowWithoutSalaryToStorage(binding.hideWithoutSalary.isChecked)
            }
        }
    }

    private fun setupSalaryInput() {
        binding.salaryEnter.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                binding.salaryEnter.clearFocus()
                true
            } else {
                false
            }
        }

        binding.salaryEnter.doOnTextChanged { text, _, _, _ ->
            val currentSalaryText = text?.toString().orEmpty()

            binding.clearSalaryButton.isVisible = currentSalaryText.isNotEmpty()

            if (currentSalaryText != initSalary) viewModel.saveSalaryToStorage(currentSalaryText)
        }

        binding.salaryEnter.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            binding.hintTitle.setTextColor(
                if (hasFocus) {
                    resources.getColor(R.color.blue, null)
                } else {
                    resources.getColor(R.color.salary_hint_color, null)
                }
            )
        }

        binding.clearSalaryButton.setOnClickListener {
            binding.salaryEnter.text?.clear()
            binding.salaryEnter.clearFocus()
            binding.clearSalaryButton.isVisible = false
            hideKeyboard()
            viewModel.saveSalaryToStorage("")
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.salaryEnter.windowToken, 0)
    }

    private fun clearIndustry() {
        binding.industryEditText.setText("")
        setHintAndIconForIndustryText("")
        viewModel.clearIndustry()
    }

    private fun clearWorkplace() {
        binding.workplaceEditText.setText("")
        setHintAndIconForWorkplaceText("")
        viewModel.clearWorkplace()
    }

    private fun setupObservers() {
        viewModel.getFilterScreenState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is FilterScreenState.Content -> {
                    displayFilterContent(state.filterParams)
                }

                is FilterScreenState.NoFilterSelected -> {
                    resetFilterUI()
                }
            }
        }

        viewModel.getIsApplyButtonVisible().observe(viewLifecycleOwner) { state ->
            binding.applyFilters.isVisible = state
        }

        viewModel.getIsResetButtonVisible().observe(viewLifecycleOwner) { state ->
            binding.resetFilters.isVisible = state
        }
    }

    private fun displayFilterContent(filterParams: FilterParams) {
        viewModel.checkIfFilterParamsUpdated(filterParams)

        initSalary = filterParams.salary ?: ""
        initCheckboxValue = filterParams.doNotShowWithoutSalary ?: false

        val workplaceText = getWorkplaceText(filterParams)

        binding.industryEditText.setText(filterParams.industry?.name)
        setHintAndIconForIndustryText(filterParams.industry?.name)

        binding.workplaceEditText.setText(workplaceText)
        setHintAndIconForWorkplaceText(workplaceText)

        binding.salaryEnter.setText(filterParams.salary ?: "")
        binding.hideWithoutSalary.isChecked = filterParams.doNotShowWithoutSalary ?: false
    }

    private fun getWorkplaceText(filterParams: FilterParams) = buildString {
        filterParams.country?.let {
            append(it.name)
        }
        filterParams.area?.let {
            append(", ${it.name}")
        }
    }

    private fun setHintAndIconForWorkplaceText(workplaceText: String) {
        val isEmptyWorkplace = workplaceText.isEmpty()
        setColorForHint(requireContext(), binding.workplaceInputLayout, isEmptyWorkplace)
        setIconForButton(binding.workplaceIcon, isEmptyWorkplace)
    }

    private fun setHintAndIconForIndustryText(industryText: String?) {
        val isEmptyIndustry = industryText.isNullOrEmpty()
        setColorForHint(requireContext(), binding.industryInputLayout, isEmptyIndustry)
        setIconForButton(binding.industryIcon, isEmptyIndustry)
    }

    private fun resetFilterUI() {
        with(binding) {
            industryEditText.setText("")
            workplaceEditText.setText("")
            salaryEnter.text?.clear()
            hideWithoutSalary.isChecked = false
            setHintAndIconForWorkplaceText("")
            setHintAndIconForIndustryText("")
        }
    }

    private fun navigateBackToSearchWithRefresh() {
        setFragmentResult(FILTERS_RESULT_KEY, bundleOf(FILTERS_RESULT_KEY to true))
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

    override fun onResume() {
        super.onResume()
        viewModel.updateFilterParameters()
    }

    companion object {
        const val FILTERS_RESULT_KEY = "apply_filters"
    }
}
