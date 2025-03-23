package ru.practicum.android.diploma.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.databinding.FragmentFavoriteBinding
import ru.practicum.android.diploma.domain.search.models.VacancyModel
import ru.practicum.android.diploma.presentation.favorites.FavoriteVacanciesViewModel
import ru.practicum.android.diploma.presentation.favorites.FavoriteVacancyState
import ru.practicum.android.diploma.ui.search.VacancyAdapter

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding: FragmentFavoriteBinding
        get() = requireNotNull(_binding) { "Binding is null" }

    private val viewModel by viewModel<FavoriteVacanciesViewModel>()
    private var vacancyAdapter: VacancyAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vacancyAdapter = VacancyAdapter(emptyList()) { vacancy ->
            openVacancy(vacancy.id)
        }
        binding.rvFavoriteVacancy.adapter = vacancyAdapter
        binding.rvFavoriteVacancy.layoutManager = LinearLayoutManager(requireContext())

        viewModel.favoriteVacancyState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavoriteVacancyState.Loading -> showLoading()
                is FavoriteVacancyState.Content -> showFavoriteVacanciesRV(state.data)
                is FavoriteVacancyState.Empty -> showEmptyState()
                is FavoriteVacancyState.Error -> showErrorState()
            }
        }

    }

    private fun showLoading() {
        binding.rvFavoriteVacancy.visibility = View.GONE
        binding.llEmptyList.visibility = View.GONE
        binding.llError.visibility = View.GONE
    }

    private fun showFavoriteVacanciesRV(data: List<VacancyModel>) {
        binding.rvFavoriteVacancy.visibility = View.VISIBLE
        binding.llEmptyList.visibility = View.GONE
        binding.llError.visibility = View.GONE
        vacancyAdapter?.updateVacancy(data)
    }

    private fun showEmptyState() {
        binding.rvFavoriteVacancy.visibility = View.GONE
        binding.llEmptyList.visibility = View.VISIBLE
        binding.llError.visibility = View.GONE
    }

    private fun showErrorState() {
        binding.rvFavoriteVacancy.visibility = View.GONE
        binding.llEmptyList.visibility = View.GONE
        binding.llError.visibility = View.VISIBLE
    }

    private fun openVacancy(vacancyId: String) {
        val directions = FavoriteFragmentDirections.actionFavoriteFragmentToVacancyFragment(vacancyId)
        findNavController().navigate(directions)
    }
}
