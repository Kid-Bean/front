package soongsil.kidbean.front.quiz.word.presentation

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.quiz.image.dto.request.ImageQuizSolveListRequest
import soongsil.kidbean.front.quiz.image.dto.response.ImageQuizSolveScoreResponse
import soongsil.kidbean.front.quiz.word.dto.request.WordQuizSolveListRequest
import soongsil.kidbean.front.quiz.word.dto.request.WordQuizUpdateRequest
import soongsil.kidbean.front.quiz.word.dto.request.WordQuizUploadRequest
import soongsil.kidbean.front.quiz.word.dto.response.WordQuizMemberDetailResponse
import soongsil.kidbean.front.quiz.word.dto.response.WordQuizMemberResponse
import soongsil.kidbean.front.quiz.word.dto.response.WordQuizSolveResponse
import soongsil.kidbean.front.quiz.word.dto.response.WordQuizSolveScoreResponse

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

    @POST("quiz/word/member/{memberId}")
    fun uploadWordQuiz(
        @Path("memberId") memberId: Long,
        @Body request: WordQuizUploadRequest
    ): Call<ResponseTemplate<Void>>

    @PUT("quiz/word/member/{memberId}/{quizId}")
    fun updateWordQuiz(
        @Path("memberId") memberId: Long,
        @Path("quizId") quizId: Long,
        @Body request: WordQuizUpdateRequest
    ): Call<ResponseTemplate<Void>>

    @DELETE("quiz/word/member/{memberId}/{quizId}")
    fun deleteWordQuiz(
        @Path("memberId") memberId: Long,
        @Path("quizId") quizId: Long
    ) :Call<ResponseTemplate<Void>>

    @GET("quiz/word/{memberId}")
    fun getRandomWordQuizByMember(
        @Path("memberId") memberId: Long
    ): Call<ResponseTemplate<WordQuizSolveResponse>>

    @POST("quiz/word/{memberId}")
    fun solveWordQuiz(
        @Path("memberId") memberId: Long,
        @Body request: WordQuizSolveListRequest
    ) :Call<ResponseTemplate<WordQuizSolveScoreResponse>>
}