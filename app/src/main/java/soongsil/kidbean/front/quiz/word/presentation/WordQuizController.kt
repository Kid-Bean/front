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
import soongsil.kidbean.front.quiz.word.dto.response.WordQuizSolveListResponse
import soongsil.kidbean.front.quiz.word.dto.response.WordQuizSolveResponse
import soongsil.kidbean.front.quiz.word.dto.response.WordQuizSolveScoreResponse

interface WordQuizController {

    @GET("quiz/word/member/{quizId}")
    fun getAnswerQuizById(
        @Path("quizId") quizId: Long
    ): Call<ResponseTemplate<WordQuizMemberDetailResponse>>

    @GET("quiz/word/member")
    fun getAllWordQuizByMember(
    ): Call<ResponseTemplate<List<WordQuizMemberResponse>>>

    @POST("quiz/word/member")
    fun uploadWordQuiz(
        @Body request: WordQuizUploadRequest
    ): Call<ResponseTemplate<Void>>

    @PUT("quiz/word/member/{quizId}")
    fun updateWordQuiz(
        @Path("quizId") quizId: Long,
        @Body request: WordQuizUpdateRequest
    ): Call<ResponseTemplate<Void>>

    @DELETE("quiz/word/member/{quizId}")
    fun deleteWordQuiz(
        @Path("quizId") quizId: Long
    ) :Call<ResponseTemplate<Void>>

    @GET("quiz/word/solve")
    fun getRandomWordQuizByMember(
    ): Call<ResponseTemplate<WordQuizSolveListResponse>>

    @POST("quiz/word/solve")
    fun solveWordQuiz(
        @Body request: WordQuizSolveListRequest
    ) :Call<ResponseTemplate<WordQuizSolveScoreResponse>>
}