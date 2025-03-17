package ru.practicum.android.diploma.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.practicum.android.diploma.databinding.FragmentVacancySearchBinding

class SearchVacancyFragment : Fragment() {

    private var _binding: FragmentVacancySearchBinding? = null
    private val binding: FragmentVacancySearchBinding
        get() = requireNotNull(_binding) { "Binding is null" }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentVacancySearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.parameters.setOnClickListener {
            val directions = SearchVacancyFragmentDirections.actionVacancySearchFragmentToFilterFragment()
            findNavController().navigate(directions)
        }
        binding.search.setOnClickListener {
            val directions = SearchVacancyFragmentDirections.actionVacancySearchFragmentToVacancyFragment()
            findNavController().navigate(directions)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
