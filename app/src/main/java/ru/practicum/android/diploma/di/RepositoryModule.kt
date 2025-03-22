package ru.practicum.android.diploma.di

import org.koin.dsl.module
import ru.practicum.android.diploma.data.favorites.impl.FavoriteVacanciesRepositoryImpl
import ru.practicum.android.diploma.data.search.impl.VacanciesRepositoryImpl
import ru.practicum.android.diploma.domain.favorites.api.FavoriteVacanciesRepository
import ru.practicum.android.diploma.domain.search.api.VacanciesRepository

val repositoryModule = module {
    single<VacanciesRepository> {
        VacanciesRepositoryImpl(get())
    }

    single<FavoriteVacanciesRepository> {
        FavoriteVacanciesRepositoryImpl(get())
    }
}
