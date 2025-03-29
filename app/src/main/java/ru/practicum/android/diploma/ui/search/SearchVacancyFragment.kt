package ru.practicum.android.diploma.ui.search

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doBeforeTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentVacancySearchBinding
import ru.practicum.android.diploma.domain.search.models.VacanciesModel
import ru.practicum.android.diploma.presentation.search.SearchScreenState
import ru.practicum.android.diploma.presentation.search.SearchVacancyViewModel
import ru.practicum.android.diploma.util.Debouncer
import ru.practicum.android.diploma.util.gone
import ru.practicum.android.diploma.util.show

class SearchVacancyFragment : Fragment() {

    private var _binding: FragmentVacancySearchBinding? = null
    private val binding get() = _binding!!

    private var debouncer: Debouncer? = null

    private var vacancyAdapter: VacancyAdapter? = null
    private var isKeyboardVisible = false
    private var keyboardListener: ViewTreeObserver.OnGlobalLayoutListener? = null

    private var query = ""

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

        debouncer = Debouncer(viewLifecycleOwner.lifecycleScope, DEBOUNCE_DELAY_MS)
        observeKeyboardVisibility()

        binding.parameters.setOnClickListener {
            openFilter()
        }

        updateSearchIcon(binding.searchEditText.text.toString())

        binding.searchOrClearIcon.setOnClickListener {
            if (binding.searchEditText.text.isNotBlank()) {
                binding.searchEditText.text.clear()
                setDefaultEmptyState()
            }
        }

        var isBackspaceClicked = false

        with(binding.searchEditText) {
            doOnTextChanged { text, _, _, _ ->
                setStartTextEditState()
                updateSearchIcon(text.toString())

                if (text.toString().isNotBlank()) {
                    debouncer?.debounce {
                        val queryString = binding.searchEditText.text.toString()
                        if (queryString.isNotBlank() && query != queryString) {
                            query = queryString
                            startSearch()
                        }
                    }
                } else {
                    setDefaultEmptyState()
                }
            }

            doBeforeTextChanged { _, _, count, after ->
                isBackspaceClicked = after < count
            }
            doAfterTextChanged { s ->
                if (isBackspaceClicked && s.toString().isEmpty()) {
                    setDefaultEmptyState()
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

            if (diff <= 0) viewModel.onLastItemReached(query)
        }

        viewModel.getSearchScreenState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is SearchScreenState.Content -> showVacancies(state.data)
                is SearchScreenState.Loading -> showLoading()
                else -> showError(state)
            }
        }
    }

    private fun showLoading() {
        val needToCenteringProgressBar = vacancyAdapter?.itemCount == 0
        if (needToCenteringProgressBar) {
            binding.recyclerViewVacancy.gone()
            val layoutParams = binding.progressBar.layoutParams as MarginLayoutParams
            layoutParams.topMargin = CENTER_OF_SCREEN_DP
            binding.progressBar.layoutParams = layoutParams
        } else {
            val layoutParams = binding.progressBar.layoutParams as MarginLayoutParams
            layoutParams.topMargin = 0
            binding.progressBar.layoutParams = layoutParams
        }
        hideAllPlaceholders()
        binding.progressBar.show()
    }

    private fun showVacancies(vacanciesModel: VacanciesModel) {
        vacancyAdapter?.addVacancies(vacanciesModel.items ?: emptyList())

        val vacanciesCount = vacanciesModel.itemsCount
        if (vacanciesCount != 0) {
            showCountNotification(message = "Найдено $vacanciesCount вакансий")
            setContentState()
        } else {
            setNothingFoundState()
        }
    }

    private fun showError(state: SearchScreenState) = when (state) {
        is SearchScreenState.Error -> setServerErrorState()
        is SearchScreenState.NoInternet -> setNoInternetState()
        is SearchScreenState.NothingFound -> setNothingFoundState()
        else -> {}
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
//        query = ""
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
        showCountNotification(message = "Таких вакансий нет")
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
        binding.progressBar.gone()
        binding.recyclerViewVacancy.show()
    }

    // Методы для работы с клавиатурой
    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    private fun observeKeyboardVisibility() {
        keyboardListener = ViewTreeObserver.OnGlobalLayoutListener {
            _binding?.let { safeBinding ->
                val rect = Rect()
                safeBinding.root.getWindowVisibleDisplayFrame(rect)
                val screenHeight = safeBinding.root.rootView.height
                val keypadHeight = screenHeight - rect.bottom

                val keyboardNowVisible = keypadHeight > screenHeight * KEYBOARD_THRESHOLD_RATIO
                if (keyboardNowVisible != isKeyboardVisible) {
                    isKeyboardVisible = keyboardNowVisible

                    val bottomNav = requireActivity().findViewById<View>(R.id.bottomNavigationView)
                    val bottomNavDivider = requireActivity().findViewById<View>(R.id.bottomNavDivider)

                    if (isKeyboardVisible) {
                        bottomNav.gone()
                        bottomNavDivider.gone()
                    } else {
                        bottomNav.show()
                        bottomNavDivider.show()
                    }
                }
            }
        }

        binding.root.viewTreeObserver.addOnGlobalLayoutListener(keyboardListener)
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

    // Методы фрагмента
    override fun onDestroyView() {
        super.onDestroyView()

        keyboardListener?.let {
            _binding?.root?.viewTreeObserver?.removeOnGlobalLayoutListener(it)
        }
        keyboardListener = null
        _binding = null
    }

    override fun onResume() {
        super.onResume()
//        val text = binding.searchEditText.text.toString()
//        updateSearchIcon(text)
//        if (text.isNotBlank()) {
//            binding.placeholderNotSearched.gone()
//        }
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        private const val DEBOUNCE_DELAY_MS = 2000L
        private const val KEYBOARD_THRESHOLD_RATIO = 0.15
        private const val CENTER_OF_SCREEN_DP = 700

    }
}
