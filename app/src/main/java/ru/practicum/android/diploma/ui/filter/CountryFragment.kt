package ru.practicum.android.diploma.ui.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.databinding.FragmentCountryBinding
import ru.practicum.android.diploma.domain.filter.models.FilterParam
import ru.practicum.android.diploma.presentation.filter.CountriesViewModel
import ru.practicum.android.diploma.presentation.filter.CountryScreenState

class CountryFragment : Fragment() {

    private var _binding: FragmentCountryBinding? = null
    private val binding: FragmentCountryBinding
        get() = requireNotNull(_binding) { "Binding is null" }

    private val viewModel by viewModel<CountriesViewModel>()
    private var adapter: CountryAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCountryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setOnClickListener {
            findNavController().navigateUp()
        }

        adapter = CountryAdapter(emptyList()) { country ->
            onCountrySelected(country)
        }
        binding.rvCountries.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCountries.adapter = adapter

        viewModel.countries.observe(viewLifecycleOwner) { countryList ->
            adapter?.updateItems(countryList)
        }

        viewModel.screenState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CountryScreenState.Error -> {
                    binding.rvCountries.isVisible = false
                    binding.llError.isVisible = true
                }

                is CountryScreenState.CountriesLoaded -> {
                    binding.rvCountries.isVisible = true
                    binding.llError.isVisible = false
                }

                else -> Unit
            }
        }

    }

    private fun onCountrySelected(country: FilterParam) {
        viewModel.selectCountry(country)
        findNavController().navigateUp()
    }
}
