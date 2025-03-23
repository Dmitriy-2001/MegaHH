package ru.practicum.android.diploma.presentation.favorites

import androidx.lifecycle.ViewModel
import ru.practicum.android.diploma.domain.favorites.api.FavoriteVacanciesInteractor

class FavoriteVacanciesViewModel(
    private val favoriteVacanciesInteractor: FavoriteVacanciesInteractor
) : ViewModel()
