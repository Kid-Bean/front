package soongsil.kidbean.front.mypage.answer.dto.response

data class SolvedAnswerQuizDetailResponse(
    val solvedId: Long,
    val answerUrl: String,
    val quizContent : String,
    val kidAnswer: String,
    val checkList : MorphemeCheckListResponse
) {
    data class MorphemeCheckListResponse(
        val checkList : List<MorphemeCheckListInfo>
    ) {
        data class MorphemeCheckListInfo(
            val checkInfoContent : String,
            val ageGroup : String,
            val isUsed : Boolean
        )
    }

}
