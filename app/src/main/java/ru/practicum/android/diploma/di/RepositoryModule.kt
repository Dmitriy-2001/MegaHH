package ru.practicum.android.diploma.di

import org.koin.dsl.module
import ru.practicum.android.diploma.data.favorites.impl.FavoriteVacanciesRepositoryImpl
import ru.practicum.android.diploma.data.filter.impl.FilterDictionaryRepositoryImpl
import ru.practicum.android.diploma.data.filter.impl.FilterRepositoryImpl
import ru.practicum.android.diploma.data.search.impl.VacanciesRepositoryImpl
import ru.practicum.android.diploma.domain.favorites.api.FavoriteVacanciesRepository
import ru.practicum.android.diploma.domain.filter.api.FilterDictionaryRepository
import ru.practicum.android.diploma.domain.filter.api.FilterRepository
import ru.practicum.android.diploma.domain.search.api.VacanciesRepository

val repositoryModule = module {
    single<VacanciesRepository> {
        VacanciesRepositoryImpl(get())
    }

    single<FavoriteVacanciesRepository> {
        FavoriteVacanciesRepositoryImpl(get())
    }

    single<FilterRepository> {
        FilterRepositoryImpl(get())
    }

    single<FilterDictionaryRepository> {
        FilterDictionaryRepositoryImpl(get())
    }
}
