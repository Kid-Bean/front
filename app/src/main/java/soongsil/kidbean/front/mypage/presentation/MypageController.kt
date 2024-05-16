package soongsil.kidbean.front.mypage.presentation

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.mypage.answer.dto.response.AllUseWordResponse
import soongsil.kidbean.front.mypage.answer.dto.response.SolvedAnswerQuizDetailResponse
import soongsil.kidbean.front.mypage.answer.dto.response.SolvedAnswerQuizListResponse
import soongsil.kidbean.front.mypage.image.dto.response.MyPageImageScoreResponse
import soongsil.kidbean.front.mypage.image.dto.response.SolvedImageDetailResponse
import soongsil.kidbean.front.mypage.image.dto.response.SolvedImageListResponse
import soongsil.kidbean.front.mypage.word.dto.response.SolvedWordDetailResponse
import soongsil.kidbean.front.mypage.word.dto.response.SolvedWordQuizListResponse

interface MypageController {

    @GET("mypage/solved/image/result")
    fun getImageScoreResult(): Call<ResponseTemplate<MyPageImageScoreResponse>>

    @GET("mypage/solved/image/list")
    fun getImageQuizList(@Query("isCorrect") isRight: Boolean)
            : Call<ResponseTemplate<SolvedImageListResponse>>

    @GET("mypage/solved/image/{solvedId}")
    fun getImageQuizDetail(
        @Path("solvedId") solvedId: Long
    ): Call<ResponseTemplate<SolvedImageDetailResponse>>

    @GET("mypage/solved/word/list")
    fun getWordQuizList():
            Call<ResponseTemplate<SolvedWordQuizListResponse>>

    @GET("mypage/solved/word/{solvedId}")
    fun getWordQuizDetail(
        @Path("solvedId") solvedId: Long
    ): Call<ResponseTemplate<SolvedWordDetailResponse>>

    @GET("mypage/solved/voice/use-word/list")
    fun getAllUseWordList()
            : Call<ResponseTemplate<AllUseWordResponse>>

    @GET("mypage/solved/voice/list")
    fun getAnswerQUizList()
            : Call<ResponseTemplate<SolvedAnswerQuizListResponse>>

    @GET("mypage/solved/voice/{solvedId}")
    fun findSolvedVoiceDetail(
        @Path("solvedId") solvedId: Long
    ): Call<ResponseTemplate<SolvedAnswerQuizDetailResponse>>
}