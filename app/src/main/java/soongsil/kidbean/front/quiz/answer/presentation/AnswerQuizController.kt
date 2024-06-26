package soongsil.kidbean.front.quiz.answer.presentation

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Part
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.quiz.answer.dto.request.AnswerQuizUpdateRequest
import soongsil.kidbean.front.quiz.answer.dto.request.AnswerQuizUploadRequest
import soongsil.kidbean.front.quiz.answer.dto.response.AnswerQuizMemberDetailResponse
import soongsil.kidbean.front.quiz.answer.dto.response.AnswerQuizMemberResponse
import soongsil.kidbean.front.quiz.answer.dto.response.AnswerQuizSolveResponse
import soongsil.kidbean.front.quiz.answer.dto.response.AnswerQuizSolveScoreResponse

interface AnswerQuizController {

    @GET("quiz/answer/member")
    fun getAllAnswerQuizByMember(
    ): Call<ResponseTemplate<List<AnswerQuizMemberResponse>>>

    @GET("quiz/answer/member/{quizId}")
    fun getAnswerQuizById(
        @Path("quizId") quizId: Long
    ): Call<ResponseTemplate<AnswerQuizMemberDetailResponse>>

    @POST("quiz/answer/member")
    fun uploadAnswerQuiz(
        @Body request: AnswerQuizUploadRequest
    ): Call<ResponseTemplate<Void>>

    @PUT("quiz/answer/member/{quizId}")
    fun updateAnswerQuiz(
        @Path("quizId") quizId: Long,
        @Body request: AnswerQuizUpdateRequest
    ): Call<ResponseTemplate<Void>>

    @DELETE("quiz/answer/member/{quizId}")
    fun deleteAnswerQuiz(
        @Path("quizId") quizId: Long
    ) :Call<ResponseTemplate<Void>>
  
   @GET("quiz/answer/solve")
    fun getRandomAnswerQuizByMember(
    ): Call<ResponseTemplate<AnswerQuizSolveResponse>>

    @Multipart
    @POST("quiz/answer/solve")
    fun solveAnswerQuiz(
        @Part record: MultipartBody.Part,
        @Part answerQuizSolvedRequest: MultipartBody.Part
    ) :Call<ResponseTemplate<AnswerQuizSolveScoreResponse>>
}