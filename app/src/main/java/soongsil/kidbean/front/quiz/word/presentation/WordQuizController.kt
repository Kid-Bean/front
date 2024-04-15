package soongsil.kidbean.front.quiz.word.presentation

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.quiz.image.dto.response.ImageQuizMemberResponse
import soongsil.kidbean.front.quiz.word.dto.request.WordQuizUpdateRequest
import soongsil.kidbean.front.quiz.word.dto.request.WordQuizUploadRequest
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
}