package ru.practicum.android.diploma.ui.filter

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentWorkplaceBinding
import ru.practicum.android.diploma.presentation.filter.WorkplaceViewModel

class WorkplaceFragment : Fragment() {

    private var _binding: FragmentWorkplaceBinding? = null
    private val binding: FragmentWorkplaceBinding
        get() = requireNotNull(_binding) { "Binding is null" }

    private val viewModel by viewModel<WorkplaceViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentWorkplaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setOnClickListener { findNavController().navigateUp() }

        viewModel.getRegionFilter().observe(viewLifecycleOwner) { region ->
            val isEmpty = region.isEmpty()
            setColorForHint(binding.regionInputLayout, isEmpty)
            setIconForButton(binding.regionIcon, isEmpty)
            binding.regionEditText.setText(region)
            binding.regionIcon.setOnClickListener {
                if (isEmpty) {
                    openRegion()
                } else {
                    binding.regionEditText.setText("")
                    viewModel.clearRegion()
                }
            }
        }

        viewModel.getCountryFilter().observe(viewLifecycleOwner) { country ->
            val isEmpty = country.isEmpty()
            setColorForHint(binding.countryInputLayout, isEmpty)
            setIconForButton(binding.countryIcon, isEmpty)
            binding.countryEditText.setText(country)
            binding.countryIcon.setOnClickListener {
                if (isEmpty) {
                    openCountry()
                } else {
                    binding.countryEditText.setText("")
                    viewModel.clearCountry()
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel.updateFilterParameters()
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateFilterParameters()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openCountry() {
        val directions = WorkplaceFragmentDirections.actionWorkplaceFragmentToCountryFragment()
        findNavController().navigate(directions)
    }

    private fun openRegion() {
        val directions = WorkplaceFragmentDirections.actionWorkplaceFragmentToRegionFragment()
        findNavController().navigate(directions)
    }

    private fun setIconForButton(button: ImageButton, isEmpty: Boolean) {
        button.setImageResource(if (isEmpty) R.drawable.ic_arrow_forward else R.drawable.ic_clear)
    }

    private fun setColorForHint(inputLayout: TextInputLayout, isEmpty: Boolean) {
        context?.let {
            val gray = ContextCompat.getColorStateList(it, R.color.gray)
            val onPrimary = getColorFromAttr(
                it,
                com.google.android.material.R.attr.colorOnPrimary
            )
            inputLayout.defaultHintTextColor = if (isEmpty) gray else onPrimary
        }
    }

    private fun getColorFromAttr(context: Context, attr: Int): ColorStateList? {
        val typedValue = TypedValue()
        val theme = context.theme
        if (theme.resolveAttribute(attr, typedValue, true)) {
            return ContextCompat.getColorStateList(context, typedValue.resourceId)
        }
        return null
    }
}
