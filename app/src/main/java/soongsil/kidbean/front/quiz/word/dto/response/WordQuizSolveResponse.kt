package soongsil.kidbean.front.quiz.word.dto.response

data class WordQuizSolveResponse(
    val answer: String,
    val quizId: Long,
    val title: String,
    val words: List<Words>

)

data class Words(
    val content: String
)