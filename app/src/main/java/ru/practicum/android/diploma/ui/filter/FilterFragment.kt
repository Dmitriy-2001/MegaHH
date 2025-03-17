package ru.practicum.android.diploma.ui.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.practicum.android.diploma.databinding.FragmentFilterBinding

class FilterFragment : Fragment() {

    private var _binding: FragmentFilterBinding? = null
    private val binding: FragmentFilterBinding
        get() = requireNotNull(_binding) { "Binding is null" }

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
        navigateUp()
        openIndustry()
        openWorkplace()
    }

    private fun navigateUp() {
        binding.toolbar.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun openIndustry() {
        binding.industryArrow.setOnClickListener {
            val directions = FilterFragmentDirections.actionFilterFragmentToIndustryFragment()
            findNavController().navigate(directions)
        }
    }

    private fun openWorkplace() {
        binding.workplaceArrow.setOnClickListener {
            val directions = FilterFragmentDirections.actionFilterFragmentToPlaceOfWorkFragment()
            findNavController().navigate(directions)
        }
    }
}
