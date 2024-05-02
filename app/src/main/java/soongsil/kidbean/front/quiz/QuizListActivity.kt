package soongsil.kidbean.front.quiz

import RetrofitImpl.retrofit
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import soongsil.kidbean.front.MainActivity
import soongsil.kidbean.front.databinding.ActivityQuizListBinding
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.login.dto.request.LoginRequest
import soongsil.kidbean.front.login.dto.request.ReissueRequest
import soongsil.kidbean.front.login.dto.response.LoginResponse
import soongsil.kidbean.front.login.dto.response.ReissueResponse
import soongsil.kidbean.front.login.presentation.LoginController
import soongsil.kidbean.front.login.ui.LoginActivity
import soongsil.kidbean.front.util.ApiClient
import java.nio.charset.Charset
import soongsil.kidbean.front.quiz.answer.ui.AnswerQuizSolveActivity
import soongsil.kidbean.front.quiz.image.ui.ImageQuizSolveActivity
import soongsil.kidbean.front.quiz.word.ui.WordQuizSolveActivity

class QuizListActivity : AppCompatActivity() {
    private lateinit var binding : ActivityQuizListBinding

    //이것도 나중에 Home 화면으로 이동
    private var preferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityQuizListBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //DB 밀었을 때 한번씩 해주세요!
//        preferences = getSharedPreferences("token", AppCompatActivity.MODE_PRIVATE)
//        val editor = preferences!!.edit()
//        editor.clear()
//        editor.apply()

        //이 아래 부분들 나중에 HomeActivity로 이동 예정
        preferences = getSharedPreferences("token", AppCompatActivity.MODE_PRIVATE)
        val accessToken = preferences?.getString("accessToken", "")
        val refreshToken = preferences?.getString("refreshToken", "")

        Log.d("exisiting access Token", accessToken.toString())
        Log.d("exisiting refresh Token", refreshToken.toString())

        //refreshToken이 없거나 만료
        if (refreshToken == "" || checkTokenExpiration(refreshToken)) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        } else {
            //refresh token은 괜찮은데 accessToken이 만료 -> 서버에 accessToken 요청
            if (accessToken == "" || checkTokenExpiration(accessToken)) {
                val request = ReissueRequest(refreshToken = refreshToken.toString())

                Log.d("access token check", "not ok")

                val loginController = retrofit.create(LoginController::class.java)
                loginController.reissueToken(request).enqueue(object :
                    Callback<ResponseTemplate<ReissueResponse>> {
                    @SuppressLint("SetTextI18n")
                    override fun onResponse(
                        call: Call<ResponseTemplate<ReissueResponse>>,
                        response: Response<ResponseTemplate<ReissueResponse>>,
                    ) {
                        if (response.isSuccessful) {
                            // 정상적으로 통신이 성공된 경우
                            Log.d("post", "onResponse 성공: " + response.body().toString())

                            val body = response.body()?.results
                            val editor = preferences!!.edit()
                            editor.putString("accessToken", body?.accessToken!!)
                            editor.apply()

                            Log.d("new token", body?.accessToken!!)

                            //홈 화면으로 다시 이동 - 여기도 나중에 HomeActivity로 바꾸기
                            val intent = Intent(this@QuizListActivity, QuizListActivity::class.java)
                            startActivity(intent)
                        } else {
                            // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                            Log.d("post", "onResponse 실패 + ${response.code()}")

                            //refresh token은 괜찮은데 DB 초기화 등 이유로 유저 정보가 없는 경우
                            preferences = getSharedPreferences("token", AppCompatActivity.MODE_PRIVATE)
                            val editor = preferences!!.edit()
                            editor.clear()
                            editor.apply()

                            val intent = Intent(this@QuizListActivity, QuizListActivity::class.java)
                            startActivity(intent)
                        }
                    }

                    override fun onFailure(call: Call<ResponseTemplate<ReissueResponse>>, t: Throwable) {
                        // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                        Log.d("post", "onFailure 에러: " + t.message.toString())
                    }
                })
            }

            Log.d("access token check", "ok")
        }

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnImage.setOnClickListener {
            val intent = Intent(this, ImageQuizSolveActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.btnWord.setOnClickListener {
            val intent = Intent(this, WordQuizSolveActivity::class.java)
            startActivity(intent)
        }

        binding.btnAnswer.setOnClickListener {
            val intent = Intent(this, AnswerQuizSolveActivity::class.java)
            startActivity(intent)
        }

        bottomSetting()
    }

    private fun bottomSetting() {
        binding.btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // 문제 풀기 화면으로 변경하기!
        binding.btnQuiz.setOnClickListener {
            val intent = Intent(this, QuizListActivity::class.java)
            startActivity(intent)
        }

        // 프로그램 화면으로 변경하기!
        binding.btnProgram.setOnClickListener {
            /*val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)*/
        }

        // 마이페이지 화면으로 변경하기!
        binding.btnProgram.setOnClickListener {
            /*val intent = Intent(this, MypageActivity::class.java)
            startActivity(intent)*/
        }
    }

    fun checkTokenExpiration(token: String?): Boolean {
        token?.let {
            val parts = it.split(".")
            if (parts.size == 3) {
                val payload = parts[1]
                val decodedBytes = Base64.decode(payload, Base64.URL_SAFE)
                val decodedString = String(decodedBytes, Charset.forName("UTF-8"))

                // JSON 파싱을 위해 Gson 라이브러리 사용
                val gson = Gson()
                val payloadMap = gson.fromJson(decodedString, Map::class.java) as Map<String, Any>
                val exp = payloadMap["exp"]?.let { num ->
                    if (num is Double) {
                        num.toLong()
                    } else {
                        null
                    }
                }

                exp?.let {
                    Log.d("exp time", it.toString())
                    val currentTime = System.currentTimeMillis() / 1000
                    Log.d("current time", currentTime.toString())

                    Log.d("time result", (it <= currentTime).toString())
                    return it <= currentTime
                }
            }
        }
        return false
    }
}