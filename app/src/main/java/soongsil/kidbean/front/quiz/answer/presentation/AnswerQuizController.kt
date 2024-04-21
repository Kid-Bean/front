package soongsil.kidbean.front.quiz.answer.presentation

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.quiz.answer.dto.response.AnswerQuizMemberResponse

interface AnswerQuizController {

    @GET("quiz/answer/member/{memberId}")
    fun getAllAnswerQuizByMember(
        @Path("memberId") memberId: Long
    ): Call<ResponseTemplate<List<AnswerQuizMemberResponse>>>
}