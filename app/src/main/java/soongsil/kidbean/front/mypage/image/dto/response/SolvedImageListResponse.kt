package soongsil.kidbean.front.mypage.image.dto.response

import java.time.LocalDateTime

data class SolvedImageListResponse(
    val solvedList : List<SolvedListInfo>
) {
    data class SolvedListInfo(
        val solvedId : Long,
        val quizCategory : String,
        val answer : String,
        val solvedTime : String
    )
}
