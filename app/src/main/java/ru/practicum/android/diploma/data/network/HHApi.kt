package ru.practicum.android.diploma.data.network

import retrofit2.http.GET
import ru.practicum.android.diploma.data.search.dto.SearchVacancyResponse

interface HHApi {
    @GET("/vacancies")
    suspend fun searchVacancy(
        query: String
    ): SearchVacancyResponse
}
