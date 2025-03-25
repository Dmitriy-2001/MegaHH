package ru.practicum.android.diploma.ui.vacancy

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentVacancyBinding
import ru.practicum.android.diploma.presentation.vacancy.VacancyState
import ru.practicum.android.diploma.presentation.vacancy.VacancyViewModel
import ru.practicum.android.diploma.util.gone
import ru.practicum.android.diploma.util.show

class VacancyFragment : Fragment() {
    private var _binding: FragmentVacancyBinding? = null
    private val binding get() = requireNotNull(_binding) { "Binding is null" }

    private val args: VacancyFragmentArgs by navArgs()
    private val vacancyId by lazy { args.vacancyId }
    private val viewModel by viewModel<VacancyViewModel> { parametersOf(vacancyId) }

    private val contentFields by lazy {
        listOf(binding.cardViewCompany, binding.experienceTitle, binding.descriptionTitle)
    }

    private val errorPlaceholders by lazy {
        listOf(
            binding.placeholderVacancyNotFound.root,
            binding.placeholderNoInternet.root,
            binding.placeholderServerError.root
        )
    }

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
            errorPlaceholders.gone()
            contentFields.gone()

            when (state) {
                is VacancyState.Content -> {
                    contentFields.show()
                    bindContent(state)
                }

                is VacancyState.NothingFound -> binding.placeholderVacancyNotFound.root.show()
                is VacancyState.NoInternet -> binding.placeholderNoInternet.root.show()
                is VacancyState.ServerError -> binding.placeholderServerError.root.show()
            }
        }
    }

    private fun bindContent(state: VacancyState.Content) = with(binding) {
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
        description.apply {
            text = HtmlCompat.fromHtml(state.data.description, HtmlCompat.FROM_HTML_MODE_LEGACY)
            movementMethod = LinkMovementMethod.getInstance()
        }

        keySkillsTitle.visibility = if (state.data.keySkills.isNotEmpty()) View.VISIBLE else View.GONE
        keySkills.text = HtmlCompat.fromHtml(
            state.data.keySkills.joinToString("<br>• ", prefix = "• "),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
