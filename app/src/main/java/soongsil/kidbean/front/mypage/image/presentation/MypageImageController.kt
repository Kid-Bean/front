package soongsil.kidbean.front.mypage.image.presentation

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.mypage.image.dto.response.MyPageImageScoreResponse
import soongsil.kidbean.front.mypage.image.dto.response.SolvedImageListResponse

interface MypageImageController {

    @GET("mypage/solved/image/result")
    fun getImageScoreResult(): Call<ResponseTemplate<MyPageImageScoreResponse>>

    @GET("mypage/solved/image/list")
    fun getImageQuizList(@Query("isCorrect") isRight: Boolean)
    : Call<ResponseTemplate<SolvedImageListResponse>>
}