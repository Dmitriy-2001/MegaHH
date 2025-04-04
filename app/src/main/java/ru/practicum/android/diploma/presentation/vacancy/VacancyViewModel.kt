package ru.practicum.android.diploma.presentation.vacancy

import android.text.Html
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.favorites.api.FavoriteVacanciesInteractor
import ru.practicum.android.diploma.domain.search.ErrorType
import ru.practicum.android.diploma.domain.search.Resource
import ru.practicum.android.diploma.domain.search.api.VacanciesInteractor

class VacancyViewModel(
    private val vacancyId: String,
    private val favoriteVacanciesInteractor: FavoriteVacanciesInteractor,
    private val vacancyInteractor: VacanciesInteractor
) : ViewModel() {

    private val vacancyState = MutableLiveData<VacancyState>()
    fun getVacancyState(): LiveData<VacancyState> = vacancyState

    private val isFavorite = MutableLiveData<Boolean>()
    fun getIsFavorite(): LiveData<Boolean> = isFavorite

    private val _isKeySkillsTitleVisible = MutableLiveData<Boolean>()
    val isKeySkillsTitleVisible: LiveData<Boolean> get() = _isKeySkillsTitleVisible

    private val _formattedKeySkills = MutableLiveData<String>()
    val formattedKeySkills: LiveData<String> get() = _formattedKeySkills

    private val _formattedDescription = MutableLiveData<String>()
    val formattedDescription: LiveData<String> get() = _formattedDescription

    private var currentJob: Job? = null

    init {
        getVacancyDetails()
    }

    private fun getVacancyDetails() {
        viewModelScope.launch {
          vacancyState.postValue(VacancyState.Loading)
            vacancyInteractor.getVacancyById(vacancyId).collect { resource ->
                vacancyState.postValue(
                    when (resource) {
                        is Resource.Success -> {
                            checkFavoriteStatus(resource.data.id)
                            _isKeySkillsTitleVisible.value = resource.data.keySkills.isNotEmpty()
                            formatKeySkills(resource.data.keySkills)
                            formatDescription(resource.data.description)
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
    }

    private fun formatDescription(description: String) {
        _formattedDescription.value = Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT).toString()
    }

    private fun formatKeySkills(keySkills: List<String>) {
        val listHtml = keySkills.joinToString(separator = "<br>• ") { it }
        _formattedKeySkills.value = Html.fromHtml("• $listHtml", Html.FROM_HTML_MODE_COMPACT).toString()
    }

    private fun checkFavoriteStatus(id: String) {
        currentJob?.cancel()

        currentJob = viewModelScope.launch {
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
