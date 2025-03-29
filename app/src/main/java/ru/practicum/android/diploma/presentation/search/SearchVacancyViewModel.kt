package ru.practicum.android.diploma.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.search.ErrorType
import ru.practicum.android.diploma.domain.search.Resource
import ru.practicum.android.diploma.domain.search.api.VacanciesInteractor
import ru.practicum.android.diploma.domain.search.models.VacanciesModel
import ru.practicum.android.diploma.util.SingleEventLiveData

class SearchVacancyViewModel(private val interactor: VacanciesInteractor) : ViewModel() {

    private var currentPage = 0
    private var maxPages = 1
    private var isNextPageLoading = false
    private var lastSearchedQuery = ""

    private val searchScreenState = SingleEventLiveData<SearchScreenState>()
    fun getSearchScreenState(): SingleEventLiveData<SearchScreenState> = searchScreenState

    fun searchVacancies(text: String) {
        viewModelScope.launch {
            prepareForSearch(text)
            searchScreenState.postValue(SearchScreenState.Loading)
            interactor.searchVacancies(text, currentPage).collect { resource ->
                handleSearchResult(resource)
            }
            isNextPageLoading = false
        }
    }

    private fun prepareForSearch(text: String) {
        isNextPageLoading = true
        if (lastSearchedQuery != text) {
            currentPage = 0
            maxPages = 1
        }
        if (lastSearchedQuery.isEmpty()) lastSearchedQuery = text
    }

    private fun handleSearchResult(resource: Resource<VacanciesModel>) {
        when (resource) {
            is Resource.Success -> {
                searchScreenState.postValue(SearchScreenState.Content(resource.data))
                if (currentPage == 0) maxPages = resource.data.pages
            }
            is Resource.Error -> {
                searchScreenState.postValue(
                    when (resource.errorType) {
                        ErrorType.NOT_FOUND -> SearchScreenState.NothingFound
                        ErrorType.NO_INTERNET -> SearchScreenState.NoInternet
                        ErrorType.SERVER_ERROR -> SearchScreenState.Error
                    }
                )
            }
        }
    }

    fun onLastItemReached(text: String) {
        if (currentPage <= maxPages && isNextPageLoading.not()) {
            currentPage++
            searchVacancies(text)
        }
    }
}
