package soongsil.kidbean.front.login.ui

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch
import soongsil.kidbean.front.databinding.ActivityLoginBinding
import soongsil.kidbean.front.quiz.QuizListActivity
import com.kakao.sdk.common.util.Utility

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
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
                        goMain(token.accessToken, token.refreshToken)
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
            goMain(token.accessToken, token.refreshToken)
        }
    }

    private fun goMain(accessToken: String, refreshToken: String) {
        //Main 으로 나중에 바꿔주기
        val intent = Intent(this, QuizListActivity::class.java).apply {
            putExtra("ACCESS_TOKEN", accessToken)
            putExtra("REFRESH_TOKEN", refreshToken);
        }
        startActivity(intent)
        finish() // LoginActivity를 종료하여 뒤로 가기를 눌렀을 때 LoginActivity로 돌아오지 않게 합니다.
    }
}