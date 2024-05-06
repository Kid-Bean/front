package soongsil.kidbean.front.login.ui

import RetrofitImpl.retrofit
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import soongsil.kidbean.front.databinding.ActivityLoginBinding
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.home.ui.MainActivity
import soongsil.kidbean.front.login.dto.request.LoginRequest
import soongsil.kidbean.front.login.dto.response.LoginResponse
import soongsil.kidbean.front.login.presentation.LoginController


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var preferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        preferences = getSharedPreferences("token", MODE_PRIVATE)
        Log.d("hash", Utility.getKeyHash(this))

        setOnClickListener()
    }

    fun setOnClickListener() {
        val context = this
        binding.mcvKakaoLogin.setOnClickListener {
            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                    if (error != null) {
                        Log.e(TAG, "카카오톡으로 로그인 실패", error)

                        // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                        // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }

                        // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                        UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                    } else if (token != null) {
                        Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                        goMain(token.accessToken)
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
            }
        }
    }

    // 카카오 로그인
    // 카카오 계정으로 로그인 공통 callback 구성
    // 카카오 톡으로 로그인 할 수 없어 카카오 계정으로 로그인 할 경우 사용됨
    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(TAG, "카카오 계정으로 로그인 실패", error)
        } else if (token != null) {
            Log.i(TAG, "카카오 계정으로 로그인 성공 ${token.accessToken}")
            goMain(token.accessToken)
        }
    }

    private fun goMain(accessToken: String) {

        val request = LoginRequest(accessToken = accessToken)

        var customAccessToken: String
        var customRefreshToken: String

        val loginController = retrofit.create(LoginController::class.java)
        loginController.socialLogin("kakao", request).enqueue(object :
            Callback<ResponseTemplate<LoginResponse>> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<ResponseTemplate<LoginResponse>>,
                response: Response<ResponseTemplate<LoginResponse>>,
            ) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    Log.d("post", "onResponse 성공: " + response.body().toString())

                    val body = response.body()?.results

                    // API로 가져온 정답 넣기
                    customAccessToken = body?.accessToken!!
                    customRefreshToken = body.refreshToken

                    Log.d("customAccessToken", customAccessToken)
                    Log.d("customRefreshToken", customRefreshToken)

                    //Editor를 preferences에 쓰겠다고 연결
                    val editor = preferences!!.edit()
                    //putString(KEY,VALUE)
                    //putString(KEY,VALUE)
                    editor.putString("accessToken", customAccessToken)
                    editor.putString("refreshToken", customRefreshToken)
                    //항상 commit & apply 를 해주어야 저장이 된다.
                    editor.apply()

                    //Main 으로 나중에 바꿔주기
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("post", "onResponse 실패 + ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseTemplate<LoginResponse>>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("post", "onFailure 에러: " + t.message.toString())
            }
        })

        finish() // LoginActivity를 종료하여 뒤로 가기를 눌렀을 때 LoginActivity로 돌아오지 않게 합니다.
    }
}