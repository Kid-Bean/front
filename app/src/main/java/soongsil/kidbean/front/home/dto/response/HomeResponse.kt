package soongsil.kidbean.front.home.dto.response

import java.time.LocalDate

data class HomeResponse(
    val name: String,
    val createdDate: String,
    val s3Url: String,
    val score: Long
)
