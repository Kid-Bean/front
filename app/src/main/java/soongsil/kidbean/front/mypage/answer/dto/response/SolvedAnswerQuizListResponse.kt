package soongsil.kidbean.front.mypage.answer.dto.response

data class SolvedAnswerQuizListResponse(
    val solvedList: List<SolvedAnswerQuizInfo>
) {
    data class SolvedAnswerQuizInfo(
        val solvedId: Long,
        val solvedTime: String,
        val title: String
    )
}
