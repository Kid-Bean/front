package soongsil.kidbean.front.quiz.word.presentation

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.quiz.image.dto.response.ImageQuizMemberResponse
import soongsil.kidbean.front.quiz.word.dto.response.WordQuizMemberDetailResponse
import soongsil.kidbean.front.quiz.word.dto.response.WordQuizMemberResponse

interface WordQuizController {

    @GET("quiz/word/member/{memberId}/{quizId}")
    fun getAnswerQuizById(
        @Path("memberId") memberId: Long,
        @Path("quizId") quizId: Long
    ): Call<ResponseTemplate<WordQuizMemberDetailResponse>>

    @GET("quiz/word/member/{memberId}")
    fun getAllWordQuizByMember(
        @Path("memberId") memberId: Long
    ): Call<ResponseTemplate<List<WordQuizMemberResponse>>>
}