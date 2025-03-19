package ru.practicum.android.diploma.data.search.dto

import com.google.gson.annotations.SerializedName

data class SearchVacancyDto(
    val id: String,
    val name: String,
    val employer: Employer,
    val address: Address,
    val salary: Salary,
    val description: String,
    val employmentForm: EmploymentForm?,
    val experience: Experience,
    val keySkills: List<KeySkill> = listOf(),
)

data class Employer(
    val name: String,
    @SerializedName("logo_urls")
    val logoUrls: LogoUrls?,
)

data class LogoUrls(
    @SerializedName("240")
    val url240: String = ""
)

data class Address(
    val city: String?
)

data class Salary(
    val currency: Currency?,
    val from: Int?,
    val to: Int?
)

data class EmploymentForm(
    val id: String,
    val name: String
)

data class Experience(
    val id: String,
    val name: String
)

data class KeySkill(
    val name: String
)

enum class Currency(val сurrencySymbol: String) {
    AZN("₼"),
    BYR("Br"),
    EUR("€"),
    GEL("₾"),
    KGS("с"),
    KZT("₸"),
    RUR("₽"),
    UAH("₴"),
    USD("$"),
    UZS("лв")
}
