package ru.practicum.android.diploma.data.filter.impl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.practicum.android.diploma.data.filter.dto.RegionDto
import ru.practicum.android.diploma.data.filter.dto.request.GetRegionRequest
import ru.practicum.android.diploma.data.filter.dto.request.IndustryRequest
import ru.practicum.android.diploma.data.filter.dto.response.IndustryResponse
import ru.practicum.android.diploma.data.search.network.NetworkClient
import ru.practicum.android.diploma.data.search.network.utils.Convertor.toModel
import ru.practicum.android.diploma.domain.filter.api.FilterDictionaryRepository
import ru.practicum.android.diploma.domain.filter.models.FilterParam
import ru.practicum.android.diploma.domain.filter.models.RegionModel
import ru.practicum.android.diploma.domain.search.Resource

class FilterDictionaryRepositoryImpl(private val networkClient: NetworkClient) : FilterDictionaryRepository {
    private var countriesCache: List<FilterParam> = emptyList()

    override fun getRegions(): Flow<Resource<List<RegionModel>>> =
        networkClient.doRequest<GetRegionRequest, List<RegionDto>>(GetRegionRequest)
            .mapResource { responseDto ->
                countriesCache = responseDto.map { regionDto ->
                    FilterParam(regionDto.id, regionDto.name)
                }
                responseDto.flatMap { regionDto ->
                    regionDto.areas
                }.map { regionDto ->
                    regionDto.toModel(countriesCache)
                }
            }

    override fun getIndustries(): Flow<Resource<List<FilterParam>>> =
        networkClient.doRequest<IndustryRequest, List<IndustryResponse>>(IndustryRequest)
            .mapResource { response ->
                response.map { industry ->
                    FilterParam(id = industry.id, name = industry.name)
                }
            }

    private inline fun <R, reified T> NetworkClient.doRequest(dto: R): Flow<Resource<T>> {
        return doRequest(dto, T::class.java)
    }

    private fun <T, X> Flow<Resource<T>>.mapResource(transform: (T) -> X): Flow<Resource<X>> =
        map { resource ->
            when (resource) {
                is Resource.Success -> Resource.Success(transform(resource.data))
                is Resource.Error -> Resource.Error(resource.errorType)
            }
        }
}
