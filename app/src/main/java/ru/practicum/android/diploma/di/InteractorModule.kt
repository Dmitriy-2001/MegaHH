package ru.practicum.android.diploma.di

import org.koin.dsl.module
import ru.practicum.android.diploma.domain.favorites.api.FavoriteVacanciesInteractor
import ru.practicum.android.diploma.domain.favorites.impl.FavoriteVacanciesInteractorImpl
import ru.practicum.android.diploma.domain.search.api.VacanciesInteractor
import ru.practicum.android.diploma.domain.search.impl.VacanciesInteractorImpl

val interactorModule = module {
    factory<VacanciesInteractor> {
        VacanciesInteractorImpl(get())
    }

    single<FavoriteVacanciesInteractor> {
        FavoriteVacanciesInteractorImpl(get())
    }
}
