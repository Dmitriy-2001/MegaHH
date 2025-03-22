package ru.practicum.android.diploma.ui.search

import android.graphics.Rect
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentVacancySearchBinding
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

        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            val currentText = text.toString()
            updateSearchIcon(currentText)

            if (currentText.isNotBlank()) {
                viewLifecycleOwner.lifecycleScope.launch {
                    debouncer.debounce {
                        if (_binding != null && binding.searchEditText.text.toString().isNotBlank()) {
                            startSearch(binding.searchEditText.text.toString())
                        }
                    }
                }
            } else {
                showPlaceholder()
            }
        }

        binding.searchEditText.setOnEditorActionListener { _, actionId, event ->
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

        vacancyAdapter = vacancyAdapter ?: VacancyAdapter(emptyList()) { vacancy ->
            openVacancy(vacancy.id)
        }
        binding.recyclerViewVacancy.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewVacancy.adapter = vacancyAdapter
    }

    override fun onResume() {
        super.onResume()
        updateSearchIcon(binding.searchEditText.text.toString())
    }

    private fun updateSearchIcon(text: String) {
        if (_binding == null) return // Проверка на null
        if (text.isNotBlank()) {
            binding.searchIcon.setImageResource(R.drawable.ic_clear)
        } else {
            binding.searchIcon.setImageResource(R.drawable.ic_search)
        }
    }

    private fun startSearch(query: String) {
        if (_binding == null) return // Проверка на null
        hidePlaceholder()
        // Передай query в ViewModel
    }

    private fun showPlaceholder() {
        if (_binding == null) return // Проверка на null
        binding.placeholder.visibility = View.VISIBLE
    }

    private fun hidePlaceholder() {
        if (_binding == null) return // Проверка на null
        binding.placeholder.visibility = View.GONE
    }

    private fun openFilter() {
        val directions = SearchVacancyFragmentDirections.actionVacancySearchFragmentToFilterFragment()
        findNavController().navigate(directions)
    }

    private fun openVacancy(vacancyId: String) {
        val directions = SearchVacancyFragmentDirections.actionVacancySearchFragmentToVacancyFragment(vacancyId)
        findNavController().navigate(directions)
    }

    @Suppress("unused") // Используется позже
    private fun showNotification(message: String) {
        if (_binding == null) return // Проверка на null
        binding.searchResultNotification.text = message
        binding.searchResultNotification.visibility = View.VISIBLE
    }

    @Suppress("unused") // Используется позже
    private fun hideNotification() {
        if (_binding == null) return // Проверка на null
        binding.searchResultNotification.visibility = View.GONE
    }

    private fun observeKeyboardVisibility() {
        keyboardListener = ViewTreeObserver.OnGlobalLayoutListener {
            if (_binding != null) { // Проверка на null
                val rect = Rect()
                binding.root.getWindowVisibleDisplayFrame(rect)
                val screenHeight = binding.root.rootView.height
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

    override fun onDestroyView() {
        keyboardListener?.let {
            binding.root.viewTreeObserver.removeOnGlobalLayoutListener(it)
        }
        keyboardListener = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val DEBOUNCE_DELAY_MS = 2000L
        private const val KEYBOARD_THRESHOLD_RATIO = 0.15
    }
}
