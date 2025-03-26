package ru.practicum.android.diploma.ui.search

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
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

    private val debouncer: Debouncer by lazy {
        Debouncer(viewLifecycleOwner.lifecycleScope, DEBOUNCE_DELAY_MS)
    }

    private var vacancyAdapter: VacancyAdapter? = null
    private var isKeyboardVisible = false
    private var keyboardListener: ViewTreeObserver.OnGlobalLayoutListener? = null

    private val viewModel by viewModel<SearchVacancyViewModel>()

    private val errorPlaceholders by lazy {
        listOf(
            binding.placeholderEmptyList.root,
            binding.placeholderNoInternet.root,
            binding.placeholderServerError.root
        )
    }

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

        observeKeyboardVisibility()

        binding.parameters.setOnClickListener {
            openFilter()
        }

        updateSearchIcon(binding.searchEditText.text.toString())

        binding.searchOrClearIcon.setOnClickListener {
            if (binding.searchEditText.text.isNotBlank()) {
                binding.searchEditText.text.clear()
                binding.placeholderNotSearched.show()
            }
        }

        with(binding.searchEditText) {
            doOnTextChanged { text, _, _, _ ->
                errorPlaceholders.gone()
                hideCountNotification()
                updateSearchIcon(text.toString())

                if (text.toString().isNotBlank()) {
                    debouncer.debounce {
                        val query = binding.searchEditText.text.toString()
                        if (query.isNotBlank()) {
                            startSearch(query)
                        }
                    }
                } else {
                    hideVacancies()
                    binding.placeholderNotSearched.show()
                }
            }

            setOnEditorActionListener { _, actionId, event ->
                val query = binding.searchEditText.text.toString()
                if ((actionId == EditorInfo.IME_ACTION_SEARCH || event?.keyCode == KeyEvent.KEYCODE_ENTER)
                    && query.isNotBlank()
                ) {
                    startSearch(query)
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

        viewModel.getSearchScreenState().observe(viewLifecycleOwner) { state ->
            errorPlaceholders.gone()
            hideCountNotification()

            when (state) {
                is SearchScreenState.Content -> showVacancies(state.data)
                is SearchScreenState.Error -> showError()
                is SearchScreenState.Loading -> showLoading()
                is SearchScreenState.NoInternet -> showNoInternetMessage()
                is SearchScreenState.NothingFound -> showNothingFoundMessage()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val text = binding.searchEditText.text.toString()
        updateSearchIcon(text)
        if (text.isNotBlank()) {
            binding.placeholderNotSearched.gone()
        }
    }

    private fun updateSearchIcon(text: String) {
        if (text.isNotBlank()) {
            binding.searchOrClearIcon.setImageResource(R.drawable.ic_clear)
        } else {
            binding.searchOrClearIcon.setImageResource(R.drawable.ic_search)
        }
    }

    private fun startSearch(query: String) {
        binding.placeholderNotSearched.gone()
        hideKeyboard()
        viewModel.searchVacancies(query)
    }

    private fun openFilter() {
        val directions = SearchVacancyFragmentDirections.actionVacancySearchFragmentToFilterFragment()
        findNavController().navigate(directions)
    }

    private fun openVacancy(vacancyId: String) {
        val directions = SearchVacancyFragmentDirections.actionVacancySearchFragmentToVacancyFragment(vacancyId)
        findNavController().navigate(directions)
    }

    private fun hideVacancies() {
        binding.recyclerViewVacancy.visibility = View.GONE
        binding.searchResultNotification.visibility = View.GONE
    }

    private fun showVacancies(vacanciesModel: VacanciesModel) {
        val vacancyList = vacanciesModel.items ?: emptyList()
        if (vacancyList.isNotEmpty()) {
            showCountNotification(message = "Найдено ${vacancyList.size} вакансий")
        } else {
            showCountNotification(message = "Таких вакансий нет")
            binding.placeholderEmptyList.root.show()
        }
        vacancyAdapter?.updateVacancy(vacancyList)
        binding.recyclerViewVacancy.show()
    }

    private fun showCountNotification(message: String) {
        binding.searchResultNotification.text = message
        binding.searchResultNotification.show()
    }

    private fun hideCountNotification() {
        binding.searchResultNotification.gone()
    }

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

    private fun showError() {
        binding.placeholderServerError.root.show()
        binding.recyclerViewVacancy.gone()
    }

    private fun showLoading() {
        binding.recyclerViewVacancy.gone()
    }

    private fun showNoInternetMessage() {
        binding.placeholderNoInternet.root.show()
        binding.recyclerViewVacancy.gone()
    }

    private fun showNothingFoundMessage() {
        binding.placeholderEmptyList.root.show()
        binding.recyclerViewVacancy.gone()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        keyboardListener?.let {
            _binding?.root?.viewTreeObserver?.removeOnGlobalLayoutListener(it)
        }
        keyboardListener = null
        _binding = null
    }

    companion object {
        private const val DEBOUNCE_DELAY_MS = 2000L
        private const val KEYBOARD_THRESHOLD_RATIO = 0.15
    }
}
