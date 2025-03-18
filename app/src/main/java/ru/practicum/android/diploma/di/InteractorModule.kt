package ru.practicum.android.diploma.di

import org.koin.dsl.module
import ru.practicum.android.diploma.domain.search.api.SearchVacanciesInteractor
import ru.practicum.android.diploma.domain.search.impl.SearchVacanciesInteractorImpl

val interactorModule = module {
    factory<SearchVacanciesInteractor> {
        SearchVacanciesInteractorImpl(get())
    }
}
