package soongsil.kidbean.front.home.ui

import RetrofitImpl.retrofit
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import soongsil.kidbean.front.databinding.ActivityMainBinding
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.home.dto.response.HomeResponse
import soongsil.kidbean.front.home.presentation.HomeController
import soongsil.kidbean.front.login.dto.request.ReissueRequest
import soongsil.kidbean.front.login.dto.response.ReissueResponse
import soongsil.kidbean.front.login.presentation.LoginController
import soongsil.kidbean.front.login.ui.LoginActivity
import soongsil.kidbean.front.mypage.MypageActivity
import soongsil.kidbean.front.quiz.QuizListActivity
import soongsil.kidbean.front.util.ApiClient
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private var preferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        ApiClient.init(this)

        binding.btnEnroll.setOnClickListener {
            val intent = Intent(this, QuizListActivity::class.java)
            startActivity(intent)
        }

        //DB 밀었을 때 한번씩 해주세요!
//        preferences = getSharedPreferences("token", AppCompatActivity.MODE_PRIVATE)
//        val editor = preferences!!.edit()
//        editor.clear()
//        editor.apply()

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
                            val intent = Intent(this@MainActivity, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                            Log.d("post", "onResponse 실패 + ${response.code()}")

                            //refresh token은 괜찮은데 DB 초기화 등 이유로 유저 정보가 없는 경우
                            preferences = getSharedPreferences("token", AppCompatActivity.MODE_PRIVATE)
                            val editor = preferences!!.edit()
                            editor.clear()
                            editor.apply()

                            val intent = Intent(this@MainActivity, MainActivity::class.java)
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

        loadInfo()

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
        binding.btnMypage.setOnClickListener {
            val intent = Intent(this, MypageActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadInfo() {
        val homeController = ApiClient.getApiClient().create(HomeController::class.java)
        homeController.getHomeInfo().enqueue(object :
            Callback<ResponseTemplate<HomeResponse>> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<ResponseTemplate<HomeResponse>>,
                response: Response<ResponseTemplate<HomeResponse>>,
            ) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    Log.d("post", "onResponse 성공: " + response.body().toString())

                    val body = response.body()?.results

                    // API로 가져온 제목 넣기
                    var name = body?.name
                    Log.d("name", name.toString())

                    // 이름이 null이거나 빈 문자열인 경우 SignUpActivity로 이동
                    if (name.isNullOrEmpty()) {
                        val intent = Intent(this@MainActivity, SignUpActivity::class.java)
                        startActivity(intent)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        return // 이후 로직을 실행하지 않고 메서드 종료
                    }

                    // 이름이 존재하는 경우 기존 로직 실행
                    binding.tvKidName.text = name

                    // createDate 문자열 예시: "2024-04-30T15:30:00"
                    val createDateStr = body?.createdDate
                    // DateTimeFormatter를 사용하여 문자열을 LocalDateTime으로 파싱
                    val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                    val createDate = LocalDateTime.parse(createDateStr, formatter)
                    val today = LocalDateTime.now()

                    // LocalDateTime을 LocalDate로 변환
                    val createDateAsLocalDate = createDate.toLocalDate()
                    val todayAsLocalDate = today.toLocalDate()

                    // 날짜 간의 차이를 계산
                    val daysBetween = ChronoUnit.DAYS.between(createDateAsLocalDate, todayAsLocalDate) + 1
                    binding.tvKidDate.text = daysBetween.toString()

                    val imageView: ImageView = binding.imgView
                    var s3Url = body?.s3Url.toString()

                    Glide.with(applicationContext)
                        .load(s3Url)
                        .into(imageView)
                    imageView.visibility = View.VISIBLE

                    var score = body?.score
                    val progressBar = binding.progressbar

                    val processedScore = if (score!! >= 100L) {
                        score % 100 // 100으로 나눈 나머지 값 (백의 자리 제외)
                    } else {
                        score // 100 미만인 경우 그대로 사용
                    }

                    progressBar.progress = score.toInt()


                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("post", "onResponse 실패 + ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseTemplate<HomeResponse>>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("post", "onFailure 에러: " + t.message.toString())
            }
        })
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