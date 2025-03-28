package ru.practicum.android.diploma.ui.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.databinding.FragmentFilterBinding
import ru.practicum.android.diploma.presentation.filter.FilterScreenState
import ru.practicum.android.diploma.presentation.filter.FilterViewModel

class FilterFragment : Fragment() {

    private var _binding: FragmentFilterBinding? = null
    private val binding: FragmentFilterBinding
        get() = requireNotNull(_binding) { "Binding is null" }

    private val viewModel by viewModel<FilterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.industryArrow.setOnClickListener { openIndustry() }
        binding.workplaceArrow.setOnClickListener { openWorkplace() }

        binding.testButton.setOnClickListener {
            viewModel.saveSalaryToFilter("700")
        }

        viewModel.getFilterScreenState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is FilterScreenState.Content -> {
                    binding.testEditText.setText(state.filterParams.salary ?: "")
                }

                is FilterScreenState.NoFilterSelected -> {}
            }
        }
    }

    private fun openIndustry() {
        val directions = FilterFragmentDirections.actionFilterFragmentToIndustryFragment()
        findNavController().navigate(directions)
    }

    private fun openWorkplace() {
        val directions = FilterFragmentDirections.actionFilterFragmentToPlaceOfWorkFragment()
        findNavController().navigate(directions)
    }
}
