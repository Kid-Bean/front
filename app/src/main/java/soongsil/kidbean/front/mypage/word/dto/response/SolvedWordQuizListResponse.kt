package soongsil.kidbean.front.mypage.word.dto.response

data class SolvedWordQuizListResponse(
    val solvedList: List<SolvedWordQuizInfo>
) {
    data class SolvedWordQuizInfo(
        val solvedId: Long,
        val solvedTime : String,
        val quizCategory: String,
        val title : String
    )
}


