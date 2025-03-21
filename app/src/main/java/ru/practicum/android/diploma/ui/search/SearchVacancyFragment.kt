package ru.practicum.android.diploma.ui.search

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentVacancySearchBinding
import ru.practicum.android.diploma.domain.search.models.VacancyModel
import ru.practicum.android.diploma.util.Debouncer

class SearchVacancyFragment : Fragment() {

    private var _binding: FragmentVacancySearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchEditText: EditText
    private lateinit var searchIcon: ImageView
    private lateinit var placeholder: View
    private lateinit var debouncer: Debouncer

    private var currentQuery: String = ""

    private var vacancyAdapter: VacancyAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentVacancySearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        searchEditText = view.findViewById(R.id.search_edit_text)
        searchIcon = view.findViewById(R.id.search_icon)
        placeholder = view.findViewById(R.id.placeholder)

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

        vacancyAdapter = vacancyAdapter ?: VacancyAdapter(emptyList()) { vacancy -> openVacancy(vacancy) }
        binding.recyclerViewVacancy.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewVacancy.adapter = vacancyAdapter
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
    }

    private fun startSearch(query: String) {
        currentQuery = query
        hidePlaceholder()
        // Запустить поиск (например, передать в ViewModel)
    }

    private fun showPlaceholder() {
        placeholder.visibility = View.VISIBLE
        // recyclerView?.visibility = View.GONE // когда появится список
    }

    private fun hidePlaceholder() {
        placeholder.visibility = View.GONE
        // recyclerView?.visibility = View.VISIBLE // когда появится список
    }

    private fun openFilter() {
        val directions = SearchVacancyFragmentDirections.actionVacancySearchFragmentToFilterFragment()
        findNavController().navigate(directions)
    }

    private fun openVacancy(vacancy: VacancyModel) {
        val directions = SearchVacancyFragmentDirections.actionVacancySearchFragmentToVacancyFragment()
        // Todo: Добавить то что будем передавать
        findNavController().navigate(directions)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
