package soongsil.kidbean.front.quiz.image.dto.response

data class ImageQuizSolveResponse(
    val answer: String,
    val s3Url: String,
    val quizId: Long
) {

}
