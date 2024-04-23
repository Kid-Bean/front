package soongsil.kidbean.front.quiz.answer.presentation

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.quiz.answer.dto.request.AnswerQuizUpdateRequest
import soongsil.kidbean.front.quiz.answer.dto.request.AnswerQuizUploadRequest
import soongsil.kidbean.front.quiz.answer.dto.response.AnswerQuizMemberDetailResponse
import soongsil.kidbean.front.quiz.answer.dto.response.AnswerQuizMemberResponse

interface AnswerQuizController {

    @GET("quiz/answer/member/{memberId}")
    fun getAllAnswerQuizByMember(
        @Path("memberId") memberId: Long
    ): Call<ResponseTemplate<List<AnswerQuizMemberResponse>>>

    @GET("quiz/answer/member/{memberId}/{quizId}")
    fun getAnswerQuizById(
        @Path("memberId") memberId: Long,
        @Path("quizId") quizId: Long
    ): Call<ResponseTemplate<AnswerQuizMemberDetailResponse>>

    @POST("quiz/answer/member/{memberId}")
    fun uploadAnswerQuiz(
        @Path("memberId") memberId: Long,
        @Body request: AnswerQuizUploadRequest
    ): Call<ResponseTemplate<Void>>

    @PUT("quiz/answer/member/{memberId}/{quizId}")
    fun updateAnswerQuiz(
        @Path("memberId") memberId: Long,
        @Path("quizId") quizId: Long,
        @Body request: AnswerQuizUpdateRequest
    ): Call<ResponseTemplate<Void>>

    @DELETE("quiz/answer/member/{memberId}/{quizId}")
    fun deleteAnswerQuiz(
        @Path("memberId") memberId: Long,
        @Path("quizId") quizId: Long
    ) :Call<ResponseTemplate<Void>>


}