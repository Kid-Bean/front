package soongsil.kidbean.front.login.presentation

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.login.dto.request.LoginRequest
import soongsil.kidbean.front.login.dto.request.ReissueRequest
import soongsil.kidbean.front.login.dto.response.LoginResponse
import soongsil.kidbean.front.login.dto.response.ReissueResponse

interface LoginController {

    @POST("auth/login/{provider}")
    fun socialLogin(
        @Path("provider") provider: String,
        @Body request: LoginRequest
    ): Call<ResponseTemplate<LoginResponse>>

    @POST("auth/reissue")
    fun reissueToken(
        @Body request: ReissueRequest
    ): Call<ResponseTemplate<ReissueResponse>>
}