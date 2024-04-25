package soongsil.kidbean.front.login.presentation

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.login.dto.request.LoginRequest
import soongsil.kidbean.front.login.dto.response.LoginResponse

interface LoginController {

    @POST("auth/login/{provider}")
    fun socialLogin(
        @Path("provider") provider: String,
        @Body request: LoginRequest
    ): Call<ResponseTemplate<LoginResponse>>
}