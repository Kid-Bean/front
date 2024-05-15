package soongsil.kidbean.front.program.presentation

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.program.dto.response.ProgramResponseList
import soongsil.kidbean.front.program.dto.response.StarResponse

interface ProgramController {

    @GET("programs")
    fun getProgramList(
        @Query("programcategory") programCategory: String,
        @Query("page") page: Int
    ): Call<ResponseTemplate<ProgramResponseList>>

    @POST("program/star/{programId}")
    fun postStar(
        @Path("programId") programId: Long
    ): Call<ResponseTemplate<StarResponse>>
}