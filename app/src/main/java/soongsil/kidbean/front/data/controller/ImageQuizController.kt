package soongsil.kidbean.front.data.controller

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import soongsil.kidbean.front.data.dto.response.ImageQuizMemberDetailResponse

interface ImageQuizController {

    @GET("quiz/image/{memberId}/{quizId}")
    fun getImageQuizById(
        @Path("memberId") memberId: Long,
        @Path("quizId") quizId: Long
        ): Call<ImageQuizMemberDetailResponse>
}