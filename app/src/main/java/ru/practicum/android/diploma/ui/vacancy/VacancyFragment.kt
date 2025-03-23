package ru.practicum.android.diploma.ui.vacancy

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.practicum.android.diploma.databinding.FragmentVacancyBinding
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
                        salary.text = state.data.salary
                        companyName.text = state.data.employer
                        companyCity.text = state.data.city

                        Glide.with(this@VacancyFragment)
                            .load(state.data.logoUrl)
                            .placeholder(R.drawable.placeholder_rv)
                            .into(companyImage)

                        experience.text = state.data.experience
                        employmentForm.text = state.data.employmentForm
                        description.text = Html.fromHtml(state.data.description, FROM_HTML_MODE_LEGACY)
                        description.movementMethod = LinkMovementMethod.getInstance()

                        if (state.data.keySkills.isNotEmpty()) keySkillsTitle.visibility = VISIBLE
                        val listHtml = state.data.keySkills.joinToString(separator = "<br>• ") { it }
                        keySkills.text = HtmlCompat.fromHtml("• $listHtml", FROM_HTML_MODE_LEGACY)
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
