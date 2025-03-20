package ru.practicum.android.diploma.presentation.vacancy

import ru.practicum.android.diploma.domain.search.Resource
import ru.practicum.android.diploma.domain.search.api.VacanciesInteractor
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.search.ErrorType

class VacancyViewModel(
    val vacancyId: String,
    private val vacancyInteractor: VacanciesInteractor
) : ViewModel() {

    private val vacancyState = MutableLiveData<VacancyState>()
    fun getVacancyState(): LiveData<VacancyState> = vacancyState

    init {
        getVacancyDetails()
    }

    fun getVacancyDetails() = viewModelScope.launch {
        vacancyInteractor.getVacancyById(vacancyId).collect { resource ->
            vacancyState.postValue(
                when (resource) {
                    is Resource.Success -> VacancyState.Content(resource.data)
                    is Resource.Error -> when (resource.errorType) {
                        ErrorType.NOT_FOUND -> VacancyState.NothingFound
                        ErrorType.NO_INTERNET -> VacancyState.NoInternet
                        else -> VacancyState.ServerError
                    }
                }
            )
        }
    }

}
