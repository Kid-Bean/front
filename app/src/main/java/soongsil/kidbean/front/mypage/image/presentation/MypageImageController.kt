package soongsil.kidbean.front.mypage.image.presentation

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.mypage.image.dto.response.MyPageImageScoreResponse

interface MypageImageController {

    @GET("mypage/solved/image/result/{memberId}")
    fun getImageScoreResult(
        @Path("memberId") memberId: Long,
    ): Call<ResponseTemplate<MyPageImageScoreResponse>>
}