package soongsil.kidbean.front.mypage.main.dto.response


data class MemberInfoResponse(
    val memberId: Long,
    val memberBirth: String,
    val memberName: String,
    val memberAge: Int,
    val memberGender: String
)
