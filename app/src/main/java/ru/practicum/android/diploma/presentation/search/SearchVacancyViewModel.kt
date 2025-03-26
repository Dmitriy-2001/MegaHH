package ru.practicum.android.diploma.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.search.ErrorType
import ru.practicum.android.diploma.domain.search.Resource
import ru.practicum.android.diploma.domain.search.api.VacanciesInteractor

class SearchVacancyViewModel(private val interactor: VacanciesInteractor) : ViewModel() {

    private var currentPage = 0
    private var maxPages = 1
    private var isNextPageLoading = false
    private var lastSearchedQuery = ""

    private val searchScreenState = MutableLiveData<SearchScreenState>()
    fun getSearchScreenState(): LiveData<SearchScreenState> = searchScreenState

    fun searchVacancies(text: String) {
        viewModelScope.launch {
            isNextPageLoading = true
            if (lastSearchedQuery != text) {
                currentPage = 0
                maxPages = 1
            }
            if (lastSearchedQuery.isEmpty()) lastSearchedQuery = text
            searchScreenState.postValue(SearchScreenState.Loading)
            interactor.searchVacancies(text, currentPage).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        searchScreenState.postValue(SearchScreenState.Content(resource.data))
                        if (currentPage == 0) maxPages = resource.data.pages
                    }

                    is Resource.Error -> searchScreenState.postValue(
                        when (resource.errorType) {
                            ErrorType.NOT_FOUND -> SearchScreenState.NothingFound
                            ErrorType.NO_INTERNET -> SearchScreenState.NoInternet
                            ErrorType.SERVER_ERROR -> SearchScreenState.Error
                        }
                    )
                }
            }
            isNextPageLoading = false
        }
    }

    fun onLastItemReached(text: String) {
        if (currentPage <= maxPages && isNextPageLoading.not()) {
            currentPage++
            searchVacancies(text)
        }
    }
}
