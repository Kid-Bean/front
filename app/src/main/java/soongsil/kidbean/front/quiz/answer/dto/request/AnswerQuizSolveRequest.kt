package soongsil.kidbean.front.quiz.answer.dto.request

data class AnswerQuizSolveTotalRequest(
    var answerQuizSolvedRequest: AnswerQuizSolveRequest,
)

data class AnswerQuizSolveRequest(
    var quizId: Long,
    var answer: String
)