package ru.practicum.android.diploma.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ru.practicum.android.diploma.databinding.FragmentVacancySearchBinding
import ru.practicum.android.diploma.domain.search.models.VacancyModel

class SearchVacancyFragment : Fragment() {

    private var _binding: FragmentVacancySearchBinding? = null
    private val binding: FragmentVacancySearchBinding
        get() = requireNotNull(_binding) { "Binding is null" }

    private var vacancyAdapter: VacancyAdapter? = null

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
            openFilter()
        }

        if (vacancyAdapter == null) {
            vacancyAdapter = VacancyAdapter(emptyList()) { vacancy -> openVacancy(vacancy) }
        }
        binding.recyclerViewVacancy.layoutManager = LinearLayoutManager(requireContext())
        vacancyAdapter?.let {
            binding.recyclerViewVacancy.adapter = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openFilter() {
        val directions = SearchVacancyFragmentDirections.actionVacancySearchFragmentToFilterFragment()
        findNavController().navigate(directions)
    }

    private fun openVacancy(vacancy: VacancyModel) {
        val directions = SearchVacancyFragmentDirections.actionVacancySearchFragmentToVacancyFragment()
        // Todo: Добавить то что будем передавать
        findNavController().navigate(directions)
    }
}
