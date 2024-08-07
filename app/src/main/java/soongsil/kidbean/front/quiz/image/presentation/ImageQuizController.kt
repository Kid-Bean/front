package soongsil.kidbean.front.quiz.image.presentation

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
import soongsil.kidbean.front.quiz.image.dto.request.ImageQuizSolveListRequest
import soongsil.kidbean.front.quiz.image.dto.response.ImageQuizMemberDetailResponse
import soongsil.kidbean.front.quiz.image.dto.response.ImageQuizMemberResponse
import soongsil.kidbean.front.quiz.image.dto.response.ImageQuizSolveListResponse
import soongsil.kidbean.front.quiz.image.dto.response.ImageQuizSolveResponse
import soongsil.kidbean.front.quiz.image.dto.response.ImageQuizSolveScoreResponse

interface ImageQuizController {

    @GET("quiz/image/member/{quizId}")
    fun getImageQuizById(
        @Path("quizId") quizId: Long
        ): Call<ResponseTemplate<ImageQuizMemberDetailResponse>>

    @GET("quiz/image/member")
    fun getAllImageQuizByMember(
    ): Call<ResponseTemplate<List<ImageQuizMemberResponse>>>

    @Multipart
    @PUT("quiz/image/member/{quizId}")
    fun updateImageQuiz(
        @Path("quizId") quizId: Long,
        @Part multipartFile: MultipartBody.Part,
        @Part("imageQuizUpdateRequest") request: RequestBody
    ): Call<ResponseTemplate<Void>>

    @Multipart
    @POST("quiz/image/member")
    fun uploadImageQuiz(
        @Part multipartFile: MultipartBody.Part,
        @Part("imageQuizUploadRequest") request: RequestBody
    ): Call<ResponseTemplate<Void>>

    @DELETE("quiz/image/member/{quizId}")
    fun deleteImageQuiz(
        @Path("quizId") quizId: Long
    ) :Call<ResponseTemplate<Void>>

    @GET("quiz/image/solve")
    fun getRandomImageQuizByMember(
    ): Call<ResponseTemplate<ImageQuizSolveListResponse>>

    @POST("quiz/image/solve")
    fun solveImageQuiz(
        @Body request: ImageQuizSolveListRequest
    ) :Call<ResponseTemplate<ImageQuizSolveScoreResponse>>

}