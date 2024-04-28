package soongsil.kidbean.front.quiz.answer.presentation

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.quiz.answer.dto.response.AnswerQuizSolveResponse
import soongsil.kidbean.front.quiz.answer.dto.response.AnswerQuizSolveScoreResponse

interface AnswerQuizController {

    @GET("quiz/answer/{memberId}")
    fun getRandomAnswerQuizByMember(
        @Path("memberId") memberId: Long
    ): Call<ResponseTemplate<AnswerQuizSolveResponse>>

    @Multipart
    @POST("quiz/answer/{memberId}")
    fun solveAnswerQuiz(
        @Path("memberId") memberId: Long,
        @Part record: MultipartBody.Part,
        @Part answerQuizSolvedRequest: MultipartBody.Part
    ) :Call<ResponseTemplate<AnswerQuizSolveScoreResponse>>
}