package soongsil.kidbean.front.quiz.word.dto.request

data class WordQuizSolveRequest(
    val quizId: Long,
    val answer: String
) {
}