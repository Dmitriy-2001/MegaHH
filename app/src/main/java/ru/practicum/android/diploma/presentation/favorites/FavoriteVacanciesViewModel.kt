package ru.practicum.android.diploma.presentation.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.favorites.api.FavoriteVacanciesInteractor

class FavoriteVacanciesViewModel(
    private val favoriteVacanciesInteractor: FavoriteVacanciesInteractor
) : ViewModel() {

    private val _favoriteVacancyState = MutableLiveData<FavoriteVacancyState>()
    val favoriteVacancyState: LiveData<FavoriteVacancyState> = _favoriteVacancyState



    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        _favoriteVacancyState.value = FavoriteVacancyState.Loading

        viewModelScope.launch {
            favoriteVacanciesInteractor.getVacanciesFavorite()
                .catch {
                    _favoriteVacancyState.postValue(FavoriteVacancyState.Error)
                }
                .collect { list ->
                    if (list.isEmpty()) {
                        _favoriteVacancyState.postValue(FavoriteVacancyState.Empty)
                    } else {
                        _favoriteVacancyState.postValue(FavoriteVacancyState.Content(list))
                    }
                }
        }
    }
}

