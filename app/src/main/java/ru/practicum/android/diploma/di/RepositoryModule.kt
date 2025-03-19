package ru.practicum.android.diploma.di

import org.koin.dsl.module
import ru.practicum.android.diploma.data.search.impl.SearchVacanciesRepositoryImpl
import ru.practicum.android.diploma.domain.search.api.SearchVacanciesRepository

val repositoryModule = module {
    single<SearchVacanciesRepository> {
        SearchVacanciesRepositoryImpl(get())
    }
}
