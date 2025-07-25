package ru.practicum.android.diploma.ui.vacancy

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentVacancyBinding
import ru.practicum.android.diploma.domain.search.models.VacancyModel
import ru.practicum.android.diploma.presentation.vacancy.VacancyState
import ru.practicum.android.diploma.presentation.vacancy.VacancyViewModel
import ru.practicum.android.diploma.util.dpToPx
import ru.practicum.android.diploma.util.gone
import ru.practicum.android.diploma.util.show

private const val CORNER_RADIUS_DP = 12f

class VacancyFragment : Fragment() {
    private var _binding: FragmentVacancyBinding? = null
    private val binding get() = requireNotNull(_binding) { "Binding is null" }

    private val args: VacancyFragmentArgs by navArgs()
    private val vacancyId by lazy { args.vacancyId }
    private val viewModel by viewModel<VacancyViewModel> { parametersOf(vacancyId) }

    private val contentFields by lazy {
        listOf(
            binding.cardViewCompany,
            binding.experienceTitle,
            binding.descriptionTitle,
            binding.ivShare,
            binding.ivFavorite
        )
    }

    private val errorPlaceholders by lazy {
        listOf(
            binding.placeholderVacancyNotFound.root,
            binding.placeholderNoInternet.root,
            binding.placeholderServerError.root
        )
    }

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
            errorPlaceholders.gone()
            contentFields.gone()
            binding.progressBar.gone()

            when (state) {
                is VacancyState.Content -> {
                    currentVacancy = state.data
                    contentFields.show()
                    bindContent(state)
                }

                is VacancyState.NothingFound -> binding.placeholderVacancyNotFound.root.show()
                is VacancyState.NoInternet -> binding.placeholderNoInternet.root.show()
                is VacancyState.ServerError -> binding.placeholderServerError.root.show()
                is VacancyState.Loading -> showLoading()
            }
        }

        viewModel.getIsFavorite().observe(viewLifecycleOwner) { isFavorite ->
            updateFavoriteIcon(isFavorite)
        }

        binding.ivFavorite.setOnClickListener {
            viewModel.changeFavoriteState()
        }
    }

    private fun bindContent(state: VacancyState.Content) = with(binding) {
        name.text = state.data.name
        salary.text = state.data.salary
        companyName.text = state.data.employer
        companyCity.text = state.data.city

        val cornerRadiusPx = dpToPx(CORNER_RADIUS_DP, requireContext())

        Glide.with(this@VacancyFragment)
            .load(state.data.logoUrl)
            .placeholder(R.drawable.placeholder_rv)
            .apply(
                RequestOptions()
                    .transform(
                        MultiTransformation(
                            CenterCrop(),
                            RoundedCorners(cornerRadiusPx)
                        )
                    )
            )
            .into(companyImage)

        experience.text = state.data.experience
        employmentForm.text = buildString {
            append(state.data.employmentForm)
            if (state.data.workFormat.isNotEmpty()) append(", ")
            append(state.data.workFormat)
        }

        viewModel.formattedDescription.observe(viewLifecycleOwner) { formattedDescription ->
            description.text = formattedDescription
        }

        viewModel.isKeySkillsTitleVisible.observe(viewLifecycleOwner) { isVisible ->
            if (isVisible) keySkillsTitle.show() else keySkillsTitle.gone()
        }

        viewModel.formattedKeySkills.observe(viewLifecycleOwner) { formattedSkills ->
            keySkills.text = formattedSkills
        }

        binding.ivShare.setOnClickListener {
            shareVacancyLink(state.data.alternateUrl)
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        binding.ivFavorite.setImageResource(
            if (isFavorite) R.drawable.ic_favorite_selected else R.drawable.ic_favorite_unselected
        )
    }

    private fun shareVacancyLink(link: String?) = link?.let {
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, link)
        }.also { startActivity(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoading() {
        binding.progressBar.show()
    }

}
