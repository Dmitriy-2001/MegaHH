package ru.practicum.android.diploma.ui.search

import android.graphics.Rect
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentVacancySearchBinding
import ru.practicum.android.diploma.util.Debouncer

class SearchVacancyFragment : Fragment() {

    private var _binding: FragmentVacancySearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchEditText: EditText
    private lateinit var searchIcon: ImageView
    private lateinit var placeholder: View
    private lateinit var debouncer: Debouncer
    private lateinit var searchNotification: TextView

    private var vacancyAdapter: VacancyAdapter? = null

    private var isKeyboardVisible = false

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

        searchEditText = view.findViewById(R.id.search_edit_text)
        searchIcon = view.findViewById(R.id.search_icon)
        placeholder = view.findViewById(R.id.placeholder)
        searchNotification = view.findViewById(R.id.search_result_notification)

        // === Отслеживание появления/скрытия клавиатуры ===
        val rootView = view
        rootView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val rect = Rect()
                rootView.getWindowVisibleDisplayFrame(rect)
                val screenHeight = rootView.rootView.height
                val keypadHeight = screenHeight - rect.bottom

                val keyboardNowVisible = keypadHeight > screenHeight * 0.15
                if (keyboardNowVisible != isKeyboardVisible) {
                    isKeyboardVisible = keyboardNowVisible
                    val bottomNav = requireActivity().findViewById<View>(R.id.bottomNavigationView)
                    val bottomNavDivider = requireActivity().findViewById<View>(R.id.bottomNavDivider)

                    val visibility = if (isKeyboardVisible) View.GONE else View.VISIBLE
                    bottomNav?.visibility = visibility
                    bottomNavDivider?.visibility = visibility
                }
            }
        })

        debouncer = Debouncer(viewLifecycleOwner.lifecycleScope, 2000)

        binding.parameters.setOnClickListener {
            openFilter()
        }

        searchEditText.doOnTextChanged { text, _, _, _ ->
            val input = text.toString()

            if (input.isNotBlank()) {
                searchIcon.setImageResource(R.drawable.ic_clear)
                searchIcon.setOnClickListener {
                    searchEditText.text.clear()
                    showPlaceholder()
                }

                debouncer.debounce {
                    if (searchEditText.text.toString().isNotBlank()) {
                        startSearch(searchEditText.text.toString())
                    }
                }
            } else {
                searchIcon.setImageResource(R.drawable.ic_search)
                searchIcon.setOnClickListener(null)
                showPlaceholder()
            }
        }

        searchEditText.setOnEditorActionListener { _, actionId, event ->
            val query = searchEditText.text.toString()
            if ((actionId == EditorInfo.IME_ACTION_SEARCH || event?.keyCode == KeyEvent.KEYCODE_ENTER)
                && query.isNotBlank()
            ) {
                startSearch(query)
                true
            } else {
                false
            }
        }

        vacancyAdapter = vacancyAdapter ?: VacancyAdapter(emptyList()) { vacancy -> openVacancy(vacancy.id) }
        binding.recyclerViewVacancy.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewVacancy.adapter = vacancyAdapter
    }

    private fun startSearch(query: String) {
        hidePlaceholder()
        // Передай query в ViewModel
    }

    private fun showPlaceholder() {
        placeholder.visibility = View.VISIBLE
    }

    private fun hidePlaceholder() {
        placeholder.visibility = View.GONE
    }

    private fun openFilter() {
        val directions = SearchVacancyFragmentDirections.actionVacancySearchFragmentToFilterFragment()
        findNavController().navigate(directions)
    }

    private fun openVacancy(vacancyId: String) {
        val directions = SearchVacancyFragmentDirections.actionVacancySearchFragmentToVacancyFragment(vacancyId)
        findNavController().navigate(directions)
    }

    private fun showNotification(message: String) {
        searchNotification.text = message
        searchNotification.visibility = View.VISIBLE
    }

    private fun hideNotification() {
        searchNotification.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
