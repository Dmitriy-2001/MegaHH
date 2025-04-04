package ru.practicum.android.diploma.ui.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.databinding.FragmentWorkplaceBinding
import ru.practicum.android.diploma.presentation.filter.WorkplaceViewModel

class WorkplaceFragment : Fragment() {

    private var _binding: FragmentWorkplaceBinding? = null
    private val binding: FragmentWorkplaceBinding
        get() = requireNotNull(_binding) { "Binding is null" }

    private val viewModel by viewModel<WorkplaceViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentWorkplaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setOnClickListener { findNavController().navigateUp() }
        binding.chooseButton.setOnClickListener { findNavController().navigateUp() }

        viewModel.getRegionFilter().observe(viewLifecycleOwner) { region ->
            val isEmpty = region.isEmpty()
            setColorForHint(requireContext(), binding.regionInputLayout, isEmpty)
            setIconForButton(binding.regionIcon, isEmpty)
            binding.regionEditText.setText(region)
            binding.regionIcon.setOnClickListener {
                if (isEmpty) {
                    openRegion()
                } else {
                    binding.regionEditText.setText("")
                    viewModel.clearRegion()
                }
            }
            setChooseButtonVisibility()
        }

        viewModel.getCountryFilter().observe(viewLifecycleOwner) { country ->
            val isEmpty = country.isEmpty()
            setColorForHint(requireContext(), binding.countryInputLayout, isEmpty)
            setIconForButton(binding.countryIcon, isEmpty)
            binding.countryEditText.setText(country)
            binding.countryIcon.setOnClickListener {
                if (isEmpty) {
                    openCountry()
                } else {
                    binding.countryEditText.setText("")
                    viewModel.clearCountry()
                }
            }
            setChooseButtonVisibility()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateFilterParameters()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openCountry() {
        val directions = WorkplaceFragmentDirections.actionWorkplaceFragmentToCountryFragment()
        findNavController().navigate(directions)
    }

    private fun openRegion() {
        val directions = WorkplaceFragmentDirections.actionWorkplaceFragmentToRegionFragment()
        findNavController().navigate(directions)
    }

    private fun setChooseButtonVisibility() {
        val isFilterEmpty = binding.regionEditText.text.isNullOrEmpty() &&
            binding.countryEditText.text.isNullOrEmpty()
        binding.chooseButton.visibility = if (isFilterEmpty) View.GONE else View.VISIBLE
    }
}
