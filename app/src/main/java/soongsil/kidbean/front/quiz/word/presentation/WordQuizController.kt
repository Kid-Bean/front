package soongsil.kidbean.front.quiz.word.presentation

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.quiz.word.dto.response.WordQuizMemberDetailResponse

interface WordQuizController {

    @GET("quiz/word/member/{memberId}/{quizId}")
    fun getAnswerQuizById(
        @Path("memberId") memberId: Long,
        @Path("quizId") quizId: Long
    ): Call<ResponseTemplate<WordQuizMemberDetailResponse>>
}