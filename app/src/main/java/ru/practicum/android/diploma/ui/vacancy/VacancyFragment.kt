package ru.practicum.android.diploma.ui.vacancy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentVacancyBinding
import ru.practicum.android.diploma.domain.search.models.VacancyModel
import ru.practicum.android.diploma.presentation.vacancy.VacancyState
import ru.practicum.android.diploma.presentation.vacancy.VacancyViewModel

class VacancyFragment : Fragment() {
    private var _binding: FragmentVacancyBinding? = null
    private val binding: FragmentVacancyBinding
        get() = requireNotNull(_binding) { "Binding is null" }

    private val args: VacancyFragmentArgs by navArgs()

    private val viewModel by viewModel<VacancyViewModel>() {
        parametersOf(vacancyId)
    }

    private val vacancyId by lazy { args.vacancyId }

    private var currentVacancy: VacancyModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentVacancyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setOnClickListener { findNavController().navigateUp() }

        viewModel.getVacancyState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is VacancyState.Content -> {
                    with(binding) {
                        name.text = state.data.name
                    }
                }

                is VacancyState.NothingFound -> {
                    TODO()
                }

                is VacancyState.NoInternet -> {
                    TODO()
                }

                is VacancyState.ServerError -> {
                    TODO()
                }
            }
        }

        viewModel.getIsFavorite().observe(viewLifecycleOwner) { isFavorite ->
            updateFavoriteIcon(isFavorite)
        }

        binding.ivFavorite.setOnClickListener {
            toggleFavorite()
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        binding.ivFavorite.setImageResource(
            if (isFavorite) R.drawable.ic_favorite_selected else R.drawable.ic_favorite_unselected
        )
    }

    private fun toggleFavorite() {
        val vacancy = currentVacancy ?: return
        val isFavorite = viewModel.getIsFavorite().value ?: false
        if (isFavorite) {
            viewModel.removeFromFavorites(vacancy)
        } else {
            viewModel.addToFavorites(vacancy)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
