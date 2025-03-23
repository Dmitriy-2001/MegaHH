package ru.practicum.android.diploma.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.presentation.search.SearchVacancyViewModel
import ru.practicum.android.diploma.presentation.vacancy.VacancyViewModel

val viewModelModule = module {
    viewModel { (vacancyId: String) ->
        VacancyViewModel(vacancyId, get())
    }
    viewModel { SearchVacancyViewModel(get()) }
}
