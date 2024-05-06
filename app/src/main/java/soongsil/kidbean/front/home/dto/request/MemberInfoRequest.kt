package soongsil.kidbean.front.home.dto.request

import java.time.LocalDate

data class MemberInfoRequest(
    val name: String,
    val gender: String,
    val birthDate: String
)
