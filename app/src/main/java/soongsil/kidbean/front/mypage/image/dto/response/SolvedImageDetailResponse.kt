package soongsil.kidbean.front.mypage.image.dto.response

data class SolvedImageDetailResponse(
    val solvedId : Long,
    val imageUrl : String,
    val answer : String,
    val kidAnswer : String
)
