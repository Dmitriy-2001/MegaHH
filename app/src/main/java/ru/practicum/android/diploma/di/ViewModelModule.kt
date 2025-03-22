package ru.practicum.android.diploma.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.presentation.favorites.FavoriteVacanciesViewModel
import ru.practicum.android.diploma.presentation.vacancy.VacancyViewModel

val viewModelModule = module {
    viewModel { (vacancyId: String) ->
        VacancyViewModel(vacancyId, get(), get())
    }

    viewModel { FavoriteVacanciesViewModel(get())}
}
