package soongsil.kidbean.front.home.presentation

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.home.dto.request.MemberInfoRequest
import soongsil.kidbean.front.home.dto.response.HomeResponse

interface HomeController {

    @GET("home")
    fun getHomeInfo(): Call<ResponseTemplate<HomeResponse>>

    @PUT("member/info")
    fun uploadMemberInfo(
        @Body request: MemberInfoRequest
    ): Call<ResponseTemplate<Void>>
}