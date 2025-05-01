package ru.practicum.android.diploma.ui.search

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.updateLayoutParams
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doBeforeTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.R.drawable.ic_parameters
import ru.practicum.android.diploma.R.drawable.ic_parameters_active
import ru.practicum.android.diploma.databinding.FragmentVacancySearchBinding
import ru.practicum.android.diploma.domain.search.models.VacanciesModel
import ru.practicum.android.diploma.presentation.search.SearchScreenState
import ru.practicum.android.diploma.presentation.search.SearchVacancyViewModel
import ru.practicum.android.diploma.ui.filter.FilterFragment.Companion.FILTERS_RESULT_KEY
import ru.practicum.android.diploma.util.Debouncer
import ru.practicum.android.diploma.util.gone
import ru.practicum.android.diploma.util.show

class SearchVacancyFragment : Fragment() {

    private var _binding: FragmentVacancySearchBinding? = null
    private val binding get() = _binding!!

    private var debouncer: Debouncer? = null

    private var vacancyAdapter: VacancyAdapter? = null

    private var query = ""

    private val viewModel by viewModel<SearchVacancyViewModel>()

    private var shouldClearOldData = false

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

        debouncer = Debouncer(viewLifecycleOwner.lifecycleScope, DEBOUNCE_DELAY_MS)

        setFragmentResultListener(FILTERS_RESULT_KEY) { _, bundle ->
            if (bundle.getBoolean(FILTERS_RESULT_KEY) && binding.searchEditText.text.isNotBlank()) {
                startSearch()
                shouldClearOldData = true
            }
        }

        binding.parameters.setOnClickListener {
            openFilter()
        }

        updateSearchIcon(binding.searchEditText.text.toString())

        binding.searchOrClearIcon.setOnClickListener {
            if (binding.searchEditText.text.isNotBlank()) {
                binding.searchEditText.text.clear()
                query = ""
                viewModel.clearSearchQuery()
            }
        }

        var isBackspaceClicked = false

        viewModel.getIsFilterEmptyState().observe(viewLifecycleOwner) { isFilterEmpty ->
            if (isFilterEmpty) {
                binding.parameters.setImageResource(ic_parameters)
            } else {
                binding.parameters.setImageResource(ic_parameters_active)
            }
        }

        with(binding.searchEditText) {
            doOnTextChanged { text, _, _, _ ->
                setStartTextEditState()
                val queryString = text.toString()
                updateSearchIcon(queryString)

                when {
                    queryString.isNotBlank() -> debouncer?.debounce {
                        if (binding.searchEditText.text.toString().isNotBlank() && query != queryString) {
                            query = queryString
                            startSearch()
                        }
                    }

                    queryString.isBlank() -> viewModel.clearSearchQuery()
                }
            }

            doBeforeTextChanged { _, _, count, after ->
                isBackspaceClicked = after < count
            }
            doAfterTextChanged { s ->
                if (isBackspaceClicked && s.toString().isEmpty()) {
                    viewModel.clearSearchQuery()
                }
            }

            setOnEditorActionListener { _, actionId, event ->
                query = binding.searchEditText.text.toString()
                if ((actionId == EditorInfo.IME_ACTION_SEARCH || event?.keyCode == KeyEvent.KEYCODE_ENTER)
                    && query.isNotBlank()
                ) {
                    startSearch()
                    true
                } else {
                    false
                }
            }
        }

        vacancyAdapter = vacancyAdapter ?: VacancyAdapter(emptyList()) { vacancy ->
            openVacancy(vacancy.id)
        }

        binding.recyclerViewVacancy.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewVacancy.adapter = vacancyAdapter

        binding.nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val view = binding.nestedScrollView.getChildAt(0)
            val diff = view.bottom - (binding.nestedScrollView.height + scrollY)

            if (diff <= 0 && binding.searchEditText.text.isNotBlank()) viewModel.onLastItemReached(query)
        }

        viewModel.getSearchScreenState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is SearchScreenState.DefaultEmptyState -> setDefaultEmptyState()
                is SearchScreenState.Content -> if (!shouldClearOldData) {
                    showVacancies(state.data)
                } else {
                    shouldClearOldData = false
                }

                is SearchScreenState.Loading -> showLoading()
                else -> showError(state)
            }
        }
    }

    // Обработка статусов из LiveData
    private fun showLoading() {
        val needToCenteringProgressBar = vacancyAdapter?.itemCount == 0
        if (needToCenteringProgressBar) {
            binding.recyclerViewVacancy.gone()
            binding.progressBar.updateLayoutParams<MarginLayoutParams> {
                topMargin = CENTER_OF_SCREEN_DP
            }
        } else {
            binding.progressBar.updateLayoutParams<MarginLayoutParams> {
                topMargin = 0
            }
        }
        hideAllPlaceholders()
        binding.progressBar.show()
    }

    private fun showVacancies(vacanciesModel: VacanciesModel) {
        vacancyAdapter?.addVacancies(vacanciesModel.items ?: emptyList())

        val loadedCount = vacanciesModel.items?.size ?: 0
        if (loadedCount == 0 || loadedCount % COUNT_OF_VACANCIES != 0) binding.progressBar.gone()

        val vacanciesCount = vacanciesModel.itemsCount

        if (vacanciesCount != 0) {
            val message = requireContext().getString(R.string.vacancies_count_found, vacanciesCount)
            showCountNotification(message = message)
            setContentState()
        } else {
            setNothingFoundState()
        }
    }

    private fun showError(state: SearchScreenState) {
        binding.progressBar.gone()
        if (vacancyAdapter?.itemCount != 0) {
            Toast.makeText(
                context,
                resources.getString(R.string.unable_to_load_new_page),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            when (state) {
                is SearchScreenState.Error -> setServerErrorState()
                is SearchScreenState.NoInternet -> setNoInternetState()
                is SearchScreenState.NothingFound -> setNothingFoundState()
                else -> {}
            }
        }
    }

    // Вспомогательные методы
    private fun startSearch() {
        hideAllPlaceholders()
        hideKeyboard()
        hideCountNotification()
        vacancyAdapter?.clearVacancy()
        viewModel.searchVacancies(query)
    }

    private fun hideVacancies() {
        binding.recyclerViewVacancy.gone()
        binding.searchResultNotification.gone()
    }

    private fun updateSearchIcon(text: String) =
        if (text.isNotBlank()) {
            binding.searchOrClearIcon.setImageResource(R.drawable.ic_clear)
        } else {
            binding.searchOrClearIcon.setImageResource(R.drawable.ic_search)
        }

    private fun hideAllPlaceholders() = listOf(
        binding.placeholderEmptyList.root,
        binding.placeholderNoInternet.root,
        binding.placeholderServerError.root,
        binding.placeholderNotSearched
    ).gone()

    private fun showCountNotification(message: String) {
        binding.searchResultNotification.text = message
        binding.searchResultNotification.show()
    }

    private fun hideCountNotification() = binding.searchResultNotification.gone()

    // Преднастроенные состояния экрана
    private fun setDefaultEmptyState() {
        hideVacancies()
        listOf(
            binding.placeholderEmptyList.root,
            binding.placeholderNoInternet.root,
            binding.placeholderServerError.root,
            binding.progressBar
        ).gone()
        binding.placeholderNotSearched.show()
    }

    private fun setStartTextEditState() {
        hideCountNotification()
        listOf(
            binding.placeholderEmptyList.root,
            binding.placeholderNoInternet.root,
            binding.placeholderServerError.root,
        ).gone()
    }

    private fun setServerErrorState() {
        hideVacancies()
        listOf(
            binding.progressBar,
            binding.placeholderEmptyList.root,
            binding.placeholderNoInternet.root,
            binding.placeholderNotSearched
        ).gone()
        binding.placeholderServerError.root.show()
    }

    private fun setNoInternetState() {
        hideVacancies()
        listOf(
            binding.progressBar,
            binding.placeholderNotSearched,
            binding.placeholderServerError.root,
            binding.placeholderEmptyList.root
        ).gone()
        binding.placeholderNoInternet.root.show()
    }

    private fun setNothingFoundState() {
        hideVacancies()
        showCountNotification(message = resources.getString(R.string.vacancies_no_such))
        listOf(
            binding.progressBar,
            binding.placeholderNotSearched,
            binding.placeholderServerError.root,
            binding.placeholderNoInternet.root
        ).gone()
        binding.placeholderEmptyList.root.show()
    }

    private fun setContentState() {
        hideAllPlaceholders()
        binding.recyclerViewVacancy.show()
    }

    // Методы для работы с клавиатурой
    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    // Навигация
    private fun openFilter() {
        val directions = SearchVacancyFragmentDirections.actionVacancySearchFragmentToFilterFragment()
        findNavController().navigate(directions)
    }

    private fun openVacancy(vacancyId: String) {
        val directions = SearchVacancyFragmentDirections.actionVacancySearchFragmentToVacancyFragment(vacancyId)
        findNavController().navigate(directions)
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateFilters()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val DEBOUNCE_DELAY_MS = 2000L
        private const val CENTER_OF_SCREEN_DP = 700
        private const val COUNT_OF_VACANCIES = 10
    }
}
