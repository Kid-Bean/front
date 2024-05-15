package soongsil.kidbean.front.program.presentation

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.program.dto.response.ProgramResponseList

interface ProgramController {

    @GET("programs")
    fun getProgramList(
        @Query("programcategory") programCategory: String,
        @Query("page") page: Int
    ): Call<ResponseTemplate<ProgramResponseList>>
}