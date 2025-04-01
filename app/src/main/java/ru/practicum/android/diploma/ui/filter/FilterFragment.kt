package ru.practicum.android.diploma.ui.filter

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getColor
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
import ru.practicum.android.diploma.util.gone
import ru.practicum.android.diploma.util.isSystemDarkMode
import ru.practicum.android.diploma.util.show

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
            navigateBackToSearchWithRefresh()
        }

        binding.resetFilters.setOnClickListener {
            viewModel.resetFilters()
        }

//        binding.salaryClear.setOnClickListener {
//            binding.salaryInput.text?.clear()
//            viewModel.saveSalaryToStorage("")
//            updateSalaryHintColor(false)
//        }

        binding.hideWithoutSalary.setOnClickListener {
            binding.hideWithoutSalary.toggle()
            if (initCheckboxValue != binding.hideWithoutSalary.isChecked)
                viewModel.saveDoNotShowWithoutSalaryToStorage(binding.hideWithoutSalary.isChecked)
        }
    }

    private fun setupSalaryInput() {
//        binding.salaryEnter.setOnFocusChangeListener { _, hasFocus ->
//            updateSalaryHintColor(hasFocus)
//        }

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
           // updateSalaryHintColor(true)
            val currentSalaryText = text?.toString() ?: ""

            if (currentSalaryText.isNotEmpty()) {
                binding.salary.endIconMode = END_ICON_CLEAR_TEXT
                binding.salary.setEndIconDrawable(R.drawable.ic_clear)
            } else {
                binding.salary.endIconMode = END_ICON_NONE
                binding.salary.endIconDrawable = null
            }

            if (currentSalaryText != initSalary) viewModel.saveSalaryToStorage(currentSalaryText)
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.salaryEnter.windowToken, 0)
    }

//    private fun updateSalaryHintColor(isFocused: Boolean) {
//        val colorResId = when {
//            isFocused -> R.color.blue
//            binding.salaryInput.text.isNotEmpty() -> R.color.black
//            else -> { R.color.white }
//            //if (isSystemDarkMode(Application())) R.color.white else R.color.gray
//        }
//        binding.salaryHint.setTextColor(getColor(requireContext(), colorResId))
//    }

    private fun updateFieldArrows() {
        val hasIndustry = binding.industry.text.toString() != getString(R.string.industry)
        val hasWorkplace = binding.workplace.text.toString() != getString(R.string.workplace)

        with(binding) {
            industryArrow.setImageResource(
                if (hasIndustry) R.drawable.ic_clear else R.drawable.ic_arrow_forward
            )
            workplaceArrow.setImageResource(
                if (hasWorkplace) R.drawable.ic_clear else R.drawable.ic_arrow_forward
            )

            industry.setTextColor(
                getColor(
                    requireContext(),
                    if (hasIndustry) R.color.black else R.color.gray
                )
            )
            workplace.setTextColor(
                getColor(
                    requireContext(),
                    if (hasWorkplace) R.color.black else R.color.gray
                )
            )
        }
    }

    private fun clearIndustry() {
        binding.industry.text = getString(R.string.industry)
        binding.industry.setTextColor(getColor(requireContext(), R.color.gray))
        binding.industryArrow.setImageResource(R.drawable.ic_arrow_forward)
        viewModel.clearIndustry()
    }

    private fun clearWorkplace() {
        binding.workplace.text = getString(R.string.workplace)
        binding.workplace.setTextColor(getColor(requireContext(), R.color.gray))
        binding.workplaceArrow.setImageResource(R.drawable.ic_arrow_forward)
        viewModel.clearWorkplace()
    }

    private fun setupObservers() {
        viewModel.getFilterScreenState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is FilterScreenState.Content -> {
                    displayFilterContent(state.filterParams)
//                    if (isInitialLoad) {
//                        currentSalaryText = state.filterParams.salary ?: ""
//                        isInitialLoad = false
//                        updateSalaryHintColor(false)
//                    }
                }

                is FilterScreenState.NoFilterSelected -> {
                    resetFilterUI()
                    //     isInitialLoad = false
                }
            }
            updateFieldArrows()
        }

        viewModel.getIsApplyButtonVisible().observe(viewLifecycleOwner) {
            binding.applyFilters.isVisible = it
        }


    }

    private fun displayFilterContent(filterParams: FilterParams) {
        initSalary = filterParams.salary ?: ""
        initCheckboxValue = filterParams.doNotShowWithoutSalary ?: false

        binding.industry.text = filterParams.industry?.name ?: getString(R.string.industry)
        updateWorkplaceText(filterParams)
        binding.salaryEnter.setText(filterParams.salary ?: "")
        binding.hideWithoutSalary.isChecked = filterParams.doNotShowWithoutSalary ?: false
        binding.resetFilters.isVisible = viewModel.isFilterEmpty().not()
    }

    private fun updateWorkplaceText(filterParams: FilterParams) {
        binding.workplace.text = when {
            filterParams.area != null -> filterParams.area.name
            filterParams.country != null -> filterParams.country.name
            else -> getString(R.string.workplace)
        }
    }

    private fun resetFilterUI() {
        with(binding) {
            industry.text = getString(R.string.industry)
            workplace.text = getString(R.string.workplace)
            salaryEnter.text?.clear()
            hideWithoutSalary.isChecked = false
            resetFilters.gone()
            applyFilters.gone()
        }
       // updateSalaryHintColor(false)
        updateFieldArrows()
    }

    private fun getWorkplaceText(filterParams: FilterParams): String {
        return when {
            filterParams.area != null -> filterParams.area.name
            filterParams.country != null -> filterParams.country.name
            else -> getString(R.string.workplace)
        }
    }

    private fun navigateBackToSearchWithRefresh() {
        setFragmentResult(FILTERS_RESULT_KEY, bundleOf(FILTERS_RESULT_KEY to true))
        findNavController().navigateUp()



//        findNavController().previousBackStackEntry?.savedStateHandle?.set(
//            SHOULD_REFRESH_SEARCH,
//            true
//        )
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
