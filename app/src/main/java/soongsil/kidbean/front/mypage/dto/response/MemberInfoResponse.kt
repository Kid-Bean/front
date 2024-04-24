package soongsil.kidbean.front.mypage.dto.response

import java.time.LocalDate

data class MemberInfoResponse(
    val memberId: Long,
    val memberBirth: String,
    val memberName: String,
    val memberAge: Int,
    val memberGender: String
)
