package ru.practicum.android.diploma.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.presentation.favorites.FavoriteVacanciesViewModel
import ru.practicum.android.diploma.presentation.filter.FilterViewModel
import ru.practicum.android.diploma.presentation.filter.RegionViewModel
import ru.practicum.android.diploma.presentation.filter.WorkplaceViewModel
import ru.practicum.android.diploma.presentation.filter.IndustryViewModel
import ru.practicum.android.diploma.presentation.search.SearchVacancyViewModel
import ru.practicum.android.diploma.presentation.vacancy.VacancyViewModel

val viewModelModule = module {
    viewModel { (vacancyId: String) ->
        VacancyViewModel(vacancyId, get(), get())
    }
    viewModel {
        SearchVacancyViewModel(get(), get())
    }
    viewModel { FavoriteVacanciesViewModel(get()) }

    viewModel { FilterViewModel(get()) }

    viewModel { IndustryViewModel(get(),get()) }

    viewModel { RegionViewModel(get(), get()) }

    viewModel { WorkplaceViewModel(get()) }
}
