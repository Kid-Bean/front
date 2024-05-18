package soongsil.kidbean.front.program.presentation

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.program.dto.response.ProgramDetailResponse
import soongsil.kidbean.front.program.dto.response.ProgramResponseList
import soongsil.kidbean.front.program.dto.response.StarResponse

interface ProgramController {

    @GET("programs")
    fun getProgramList(
        @Query("programCategoryList") programCategory: List<String>,
        @Query("page") page: Int
    ): Call<ResponseTemplate<ProgramResponseList>>

    @GET("programs/star")
    fun getStarProgramList(
        @Query("page") page: Int
    ): Call<ResponseTemplate<ProgramResponseList>>

    @POST("program/star/{programId}")
    fun postStar(
        @Path("programId") programId: Long
    ): Call<ResponseTemplate<StarResponse>>

    @GET("programs/{programId}")
    fun getProgramDetail(
        @Path("programId") programId: Long
    ): Call<ResponseTemplate<ProgramDetailResponse>>

    @Multipart
    @POST("programs")
    fun uploadProgram(
        @Part programImage: MultipartBody.Part,
        @Part departmentImage: MultipartBody.Part,
        @Part("enrollProgramRequest") request: RequestBody
    ): Call<ResponseTemplate<Void>>

    @Multipart
    @PUT("programs")
    fun editProgram(
        @Part programImage: MultipartBody.Part,
        @Part departmentImage: MultipartBody.Part,
        @Part("updateProgramRequest") request: RequestBody
    ): Call<ResponseTemplate<Void>>

    @DELETE("programs/{programId}")
    fun deleteProgram(
        @Path("programId") programId: Long
    ): Call<ResponseTemplate<Void>>
}