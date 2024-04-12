package soongsil.kidbean.front.quiz.word.dto.response

data class WordQuizMemberDetailResponse(
    val title: String,
    val answer: String,
    val words: List<Words>
) {
    data class Words(
        val content: String
    )
}
