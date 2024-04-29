package soongsil.kidbean.front.member.presentation

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.mypage.main.dto.response.MemberInfoResponse

interface MemberController {

    @GET("member/info")
    fun getImageQuizById(
        ): Call<ResponseTemplate<MemberInfoResponse>>
}