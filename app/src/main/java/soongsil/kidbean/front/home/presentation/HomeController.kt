package soongsil.kidbean.front.home.presentation

import retrofit2.Call
import retrofit2.http.GET
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.home.dto.response.HomeResponse

interface HomeController {

    @GET("home")
    fun getHomeInfo(): Call<ResponseTemplate<HomeResponse>>
}