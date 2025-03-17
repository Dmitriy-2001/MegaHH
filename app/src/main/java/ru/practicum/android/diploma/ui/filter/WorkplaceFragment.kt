package ru.practicum.android.diploma.ui.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.practicum.android.diploma.databinding.FragmentWorkplaceBinding

class WorkplaceFragment : Fragment() {

    private var _binding: FragmentWorkplaceBinding? = null
    private val binding: FragmentWorkplaceBinding
        get() = requireNotNull(_binding) { "Binding is null" }

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
        navigateUp()
        openCountry()
        openRegion()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun navigateUp() {
        binding.toolbar.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun openCountry() {
        binding.countryArrow.setOnClickListener {
            val directions = WorkplaceFragmentDirections.actionWorkplaceFragmentToCountryFragment()
            findNavController().navigate(directions)
        }
    }

    private fun openRegion() {
        binding.regionArrow.setOnClickListener {
            val directions = WorkplaceFragmentDirections.actionWorkplaceFragmentToRegionFragment()
            findNavController().navigate(directions)
        }
    }
}
