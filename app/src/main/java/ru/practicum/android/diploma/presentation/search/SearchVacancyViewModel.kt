package ru.practicum.android.diploma.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.search.ErrorType
import ru.practicum.android.diploma.domain.search.Resource
import ru.practicum.android.diploma.domain.search.api.VacanciesInteractor
import ru.practicum.android.diploma.domain.search.models.VacanciesModel

class SearchVacancyViewModel(private val interactor: VacanciesInteractor) : ViewModel() {

    private val _vacanciesLiveData = MutableLiveData<Resource<VacanciesModel>>()
    val vacanciesLiveData: LiveData<Resource<VacanciesModel>> = _vacanciesLiveData

    private val searchScreenState = MutableLiveData<SearchScreenState>()
    fun getSearchScreenState(): LiveData<SearchScreenState> = searchScreenState

    fun searchVacancies(text: String) {
        viewModelScope.launch {
            searchScreenState.postValue(SearchScreenState.Loading)
            interactor.searchVacancies(text).collectLatest { resource ->
                searchScreenState.postValue(
                    when (resource) {
                        is Resource.Success -> SearchScreenState.Content(resource.data)
                        is Resource.Error -> when (resource.errorType) {
                            ErrorType.NOT_FOUND -> SearchScreenState.NothingFound
                            ErrorType.NO_INTERNET -> SearchScreenState.NoInternet
                            ErrorType.SERVER_ERROR -> SearchScreenState.Error
                        }

                    }
                )
            }
        }
    }
}
