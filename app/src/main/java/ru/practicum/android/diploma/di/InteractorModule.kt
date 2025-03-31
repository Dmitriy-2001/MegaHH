package ru.practicum.android.diploma.di

import org.koin.dsl.module
import ru.practicum.android.diploma.domain.favorites.api.FavoriteVacanciesInteractor
import ru.practicum.android.diploma.domain.favorites.impl.FavoriteVacanciesInteractorImpl
import ru.practicum.android.diploma.domain.filter.api.FilterDictionaryInteractor
import ru.practicum.android.diploma.domain.filter.api.FilterInteractor
import ru.practicum.android.diploma.domain.filter.impl.FilterDictionaryInteractorImpl
import ru.practicum.android.diploma.domain.filter.impl.FilterInteractorImpl
import ru.practicum.android.diploma.domain.search.api.VacanciesInteractor
import ru.practicum.android.diploma.domain.search.impl.VacanciesInteractorImpl

val interactorModule = module {
    factory<VacanciesInteractor> {
        VacanciesInteractorImpl(get())
    }

    single<FavoriteVacanciesInteractor> {
        FavoriteVacanciesInteractorImpl(get())
    }

    single<FilterInteractor> {
        FilterInteractorImpl(get())
    }

    single<FilterDictionaryInteractor> {
        FilterDictionaryInteractorImpl(get())
    }
}
