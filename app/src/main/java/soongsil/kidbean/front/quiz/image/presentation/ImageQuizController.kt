package soongsil.kidbean.front.quiz.image.presentation

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import soongsil.kidbean.front.quiz.image.dto.response.ImageQuizMemberDetailResponse

interface ImageQuizController {

    @GET("quiz/image/member/{memberId}/{quizId}")
    fun getImageQuizById(
        @Path("memberId") memberId: Long,
        @Path("quizId") quizId: Long
        ): Call<ImageQuizMemberDetailResponse>

    @Multipart
    @PUT("quiz/image/member/{memberId}/{quizId}")
    fun updateImageQuiz(
        @Path("memberId") memberId: Long,
        @Path("quizId") quizId: Long,
        @Part image: MultipartBody.Part,
        @Part("imageQuizUpdateRequest") request: RequestBody
    ): Call<Void>

    @Multipart
    @POST("quiz/image/member/{memberId}")
    fun uploadImageQuiz(
        @Path("memberId") memberId: Long,
        @Part image: MultipartBody.Part,
        @Part("imageQuizUploadRequest") request: RequestBody
    ): Call<Void>

    @DELETE("quiz/image/member/{memberId}/{quizId}")
    fun deleteImageQuiz(
        @Path("memberId") memberId: Long,
        @Path("quizId") quizId: Long
    ) :Call<Void>
}