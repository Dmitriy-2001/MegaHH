package ru.practicum.android.diploma.data.search.dto

import com.google.gson.annotations.SerializedName

data class SearchVacancyDto(
    val id: String,
    val name: String,
    val employer: Employer,
    val address: Address?,
    val salary: Salary?,
    val description: String?,
    val employment: EmploymentForm?,
    val experience: Experience,
    @SerializedName("key_skills")
    val keySkills: List<KeySkill>? = listOf(),
    val area: Area,
    @SerializedName("work_format")
    val workFormat: List<WorkFormat>? = listOf()
)

data class Employer(
    val name: String,
    @SerializedName("logo_urls")
    val logoUrls: LogoUrls?,
    val alternateUrl: String?
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

data class Area(
    val name: String
)

data class WorkFormat(
    val name: String
)

enum class Currency(val symbol: String) {
    AZN(" ₼"),
    BYR(" Br"),
    EUR(" €"),
    GEL(" ₾"),
    KGS(" с"),
    KZT(" ₸"),
    RUR(" ₽"),
    UAH(" ₴"),
    USD(" $"),
    UZS(" лв")
}
