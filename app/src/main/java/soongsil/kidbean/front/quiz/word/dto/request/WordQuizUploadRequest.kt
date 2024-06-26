package soongsil.kidbean.front.quiz.word.dto.request

data class WordQuizUploadRequest(
    val title: String,
    val answer: String,
    val words: List<Words>
) {
    data class Words(
        val content: String
    )
}
