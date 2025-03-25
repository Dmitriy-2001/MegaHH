package ru.practicum.android.diploma.presentation.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.favorites.api.FavoriteVacanciesInteractor
import ru.practicum.android.diploma.domain.search.models.VacancyModel

class FavoriteVacanciesViewModel(
    private val favoriteVacanciesInteractor: FavoriteVacanciesInteractor
) : ViewModel() {

    private val _favoriteVacancyState = MutableLiveData<FavoriteVacancyState>()
    val favoriteVacancyState: LiveData<FavoriteVacancyState> = _favoriteVacancyState

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        _favoriteVacancyState.value = FavoriteVacancyState.Loading
        viewModelScope.launch {
            favoriteVacanciesInteractor.getVacanciesFavorite().catch {
                _favoriteVacancyState.postValue(FavoriteVacancyState.Error)
            }.collect { list -> handleFavoritesResult(list) }
        }
    }

    private fun handleFavoritesResult(list: List<VacancyModel>) {
        _favoriteVacancyState.postValue(
            if (list.isEmpty()) FavoriteVacancyState.Empty else FavoriteVacancyState.Content(list)
        )
    }
}
