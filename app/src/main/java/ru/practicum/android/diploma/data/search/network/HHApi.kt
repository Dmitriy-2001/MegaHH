package ru.practicum.android.diploma.data.search.network

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap
import ru.practicum.android.diploma.BuildConfig
import ru.practicum.android.diploma.data.filter.dto.CountryDto
import ru.practicum.android.diploma.data.filter.dto.RegionDto
import ru.practicum.android.diploma.data.filter.dto.response.IndustryResponse
import ru.practicum.android.diploma.data.search.dto.response.GetVacancyDetailsResponse
import ru.practicum.android.diploma.data.search.dto.response.SearchVacanciesResponse

interface HHApi {
    @Headers(authHeader, userAgentHeader)
    @GET("/vacancies")
    suspend fun searchVacancy(
        @Query("page") page: Int?,
        @Query("per_page") perPage: Int?,
        @Query("text") text: String,
        @QueryMap filter: Map<String, String>
    ): SearchVacanciesResponse

    @Headers(authHeader, userAgentHeader)
    @GET("/vacancies/{vacancy_id}")
    suspend fun getVacancyDetails(
        @Path("vacancy_id") vacancyId: String
    ): GetVacancyDetailsResponse

    @GET("/industries")
    suspend fun getIndustries(): List<IndustryResponse>

    @Headers(authHeader, userAgentHeader)
    @GET("/areas/countries")
    suspend fun getCountries(): List<CountryDto>

    @Headers(authHeader, userAgentHeader)
    @GET("/areas")
    suspend fun getAreas(): List<RegionDto>

    companion object {
        private const val authHeader = "Authorization: Bearer ${BuildConfig.HH_ACCESS_TOKEN}"
        private const val userAgentHeader = "HH-User-Agent: megaHH (bizim734@gmail.com)"
    }
}
