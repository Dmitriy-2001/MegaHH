package ru.practicum.android.diploma.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.databinding.FragmentVacancySearchBinding
import ru.practicum.android.diploma.domain.search.models.VacanciesModel
import ru.practicum.android.diploma.presentation.search.SearchScreenState
import ru.practicum.android.diploma.presentation.search.SearchVacancyViewModel

class SearchVacancyFragment : Fragment() {

    private var _binding: FragmentVacancySearchBinding? = null
    private val binding: FragmentVacancySearchBinding
        get() = requireNotNull(_binding) { "Binding is null" }

    private var vacancyAdapter: VacancyAdapter? = null

    private val viewModel by viewModel<SearchVacancyViewModel>()

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

        vacancyAdapter = vacancyAdapter ?: VacancyAdapter(emptyList()) { vacancy -> openVacancy(vacancy.id) }
        binding.recyclerViewVacancy.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewVacancy.adapter = vacancyAdapter

        viewModel.getSearchScreenState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is SearchScreenState.Content -> showVacancies(state.data)
                is SearchScreenState.Error -> TODO()
                is SearchScreenState.Loading -> TODO()
                is SearchScreenState.NoInternet -> TODO()
                is SearchScreenState.NothingFound -> TODO()
            }
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

    private fun openVacancy(vacancyId: String) {
        val directions = SearchVacancyFragmentDirections.actionVacancySearchFragmentToVacancyFragment(vacancyId)
        findNavController().navigate(directions)
    }

    private fun showVacancies(vacanciesModel: VacanciesModel) {
        val vacancyList = vacanciesModel.items ?: emptyList()
        vacancyAdapter?.updateVacancy(vacancyList)
    }
}
