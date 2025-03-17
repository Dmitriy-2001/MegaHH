package ru.practicum.android.diploma.ui.root

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import ru.practicum.android.diploma.BuildConfig
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.ActivityRootBinding

class RootActivity : AppCompatActivity() {

    val binding by lazy {
        ActivityRootBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.filterFragment,
                R.id.workplaceFragment,
                R.id.countryFragment,
                R.id.regionFragment,
                R.id.vacancyFragment,
                R.id.industryFragment -> {
                    changeBottomNavigationViewVisibility(false)
                }

                else -> {
                    changeBottomNavigationViewVisibility(true)
                }
            }
        }
    }

    private fun changeBottomNavigationViewVisibility(isVisible: Boolean) {
        binding.bottomNavigationView.isVisible = isVisible
        binding.bottomNavDivider.isVisible = isVisible
    }
}
