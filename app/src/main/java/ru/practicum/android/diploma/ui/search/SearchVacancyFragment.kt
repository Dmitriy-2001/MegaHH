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

        binding.searchIcon.setOnClickListener {
            if (binding.searchEditText.text.isNotBlank()) {
                binding.searchEditText.text.clear()
                showPlaceholder()
            }
        }

        with(binding.searchEditText) {
            doOnTextChanged { text, _, _, _ ->
                val currentText = text.toString()
                updateSearchIcon(currentText)

                if (currentText.isNotBlank()) {
                    debouncer.debounce {
                        val query = binding.searchEditText.text.toString()
                        if (query.isNotBlank()) {
                            startSearch(query)
                        }
                    }
                } else {
                    showPlaceholder()
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
            println("State: $state")
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
            hidePlaceholder()
        }
    }

    private fun updateSearchIcon(text: String) {
        if (text.isNotBlank()) {
            binding.searchIcon.setImageResource(R.drawable.ic_clear)
        } else {
            binding.searchIcon.setImageResource(R.drawable.ic_search)
        }
    }

    private fun startSearch(query: String) {
        hidePlaceholder()
        hideKeyboard()
        viewModel.searchVacancies(query)
    }

    private fun showPlaceholder() {
        binding.placeholderNotSearched.visibility = View.VISIBLE
    }

    private fun hidePlaceholder() {
        binding.placeholderNotSearched.visibility = View.GONE
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
        if (vacancyList.isNotEmpty()) {
            showNotification(message = "Найдено ${vacancyList.size} вакансий")
            binding.searchResultNotification.visibility = View.VISIBLE
        } else {
            hideNotification()
        }
        vacancyAdapter?.updateVacancy(vacancyList)
        binding.recyclerViewVacancy.visibility = View.VISIBLE
    }

    @Suppress("unused")
    private fun showNotification(message: String) {
        binding.searchResultNotification.text = message
        binding.searchResultNotification.visibility = View.VISIBLE
    }

    @Suppress("unused")
    private fun hideNotification() {
        binding.searchResultNotification.visibility = View.GONE
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

                    val visibility = if (isKeyboardVisible) View.GONE else View.VISIBLE
                    bottomNav?.visibility = visibility
                    bottomNavDivider?.visibility = visibility
                }
            }
        }

        binding.root.viewTreeObserver.addOnGlobalLayoutListener(keyboardListener)
    }

    private fun showError() {
        binding.recyclerViewVacancy.visibility = View.GONE
    }

    private fun showLoading() {
        binding.recyclerViewVacancy.visibility = View.GONE
    }

    private fun showNoInternetMessage() {
        binding.recyclerViewVacancy.visibility = View.GONE
    }

    private fun showNothingFoundMessage() {
        binding.recyclerViewVacancy.visibility = View.GONE
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
