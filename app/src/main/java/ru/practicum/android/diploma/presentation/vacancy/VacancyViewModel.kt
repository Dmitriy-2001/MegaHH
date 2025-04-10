package ru.practicum.android.diploma.presentation.vacancy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.favorites.api.FavoriteVacanciesInteractor
import ru.practicum.android.diploma.domain.search.ErrorType
import ru.practicum.android.diploma.domain.search.Resource
import ru.practicum.android.diploma.domain.search.api.VacanciesInteractor

class VacancyViewModel(
    val vacancyId: String,
    private val favoriteVacanciesInteractor: FavoriteVacanciesInteractor,
    private val vacancyInteractor: VacanciesInteractor
) : ViewModel() {

    private val vacancyState = MutableLiveData<VacancyState>()
    fun getVacancyState(): LiveData<VacancyState> = vacancyState

    private val isFavorite = MutableLiveData<Boolean>()
    fun getIsFavorite(): LiveData<Boolean> = isFavorite

    init {
        getVacancyDetails()
    }

    fun getVacancyDetails() = viewModelScope.launch {
        vacancyInteractor.getVacancyById(vacancyId).collect { resource ->
            vacancyState.postValue(
                when (resource) {
                    is Resource.Success -> {
                        checkFavoriteStatus(resource.data.id)
                        VacancyState.Content(resource.data)
                    }

                    is Resource.Error -> when (resource.errorType) {
                        ErrorType.NOT_FOUND -> VacancyState.NothingFound
                        ErrorType.NO_INTERNET -> {
                            favoriteVacanciesInteractor.getVacancyFavoriteById(vacancyId).firstOrNull()
                                ?.let { localVacancy ->
                                    checkFavoriteStatus(vacancyId)
                                    VacancyState.Content(localVacancy)
                                } ?: VacancyState.NoInternet
                        }

                        else -> VacancyState.ServerError
                    }
                }
            )
        }
    }

    private fun checkFavoriteStatus(id: String) {
        viewModelScope.launch {
            val isFav = favoriteVacanciesInteractor.checkIfVacancyIsFavorite(id)
            isFavorite.postValue(isFav)
        }
    }

    fun changeFavoriteState() {
        val vacancy = (vacancyState.value as? VacancyState.Content)?.data ?: return
        val currentlyFavorite = isFavorite.value ?: false

        viewModelScope.launch {
            if (currentlyFavorite) {
                favoriteVacanciesInteractor.removeVacancyFromFavorite(vacancy)
                isFavorite.postValue(false)
            } else {
                favoriteVacanciesInteractor.addVacancyToFavorite(vacancy)
                isFavorite.postValue(true)
            }
        }
    }
}
