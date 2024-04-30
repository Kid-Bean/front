package soongsil.kidbean.front.home.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import soongsil.kidbean.front.R
import soongsil.kidbean.front.databinding.ActivityMainBinding
import soongsil.kidbean.front.databinding.ActivityMyQuizBinding
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.home.dto.response.HomeResponse
import soongsil.kidbean.front.home.presentation.HomeController
import soongsil.kidbean.front.mypage.MypageActivity
import soongsil.kidbean.front.quiz.QuizListActivity
import soongsil.kidbean.front.quiz.answer.dto.response.AnswerQuizMemberDetailResponse
import soongsil.kidbean.front.quiz.answer.presentation.AnswerQuizController
import soongsil.kidbean.front.quiz.answer.ui.AnswerQuizUploadActivity
import soongsil.kidbean.front.util.ApiClient
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        ApiClient.init(this)

        binding.btnEnroll.setOnClickListener {
            val intent = Intent(this, QuizListActivity::class.java)
            startActivity(intent)
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
                    var name = body?.name.toString()
                    Log.d("name", name)

                    // 이름이 null이거나 빈 문자열인 경우 SignUpActivity로 이동
                    if (name.isEmpty()) {
                        val intent = Intent(this@MainActivity, SignUpActivity::class.java)
                        startActivity(intent)
                        finish() // 현재 Activity 종료
                        return // 이후 로직을 실행하지 않고 메서드 종료
                    }

                    // 이름이 존재하는 경우 기존 로직 실행
                    binding.tvKidName.text = name

                    // createDate 문자열 예시: "2024-04-30"
                    val createDateStr = body?.createDate.toString()
                    // DateTimeFormatter를 사용하여 문자열을 LocalDate로 파싱
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val createDate = LocalDate.parse(createDateStr, formatter)
                    val today = LocalDate.now()
                    val daysBetween = ChronoUnit.DAYS.between(createDate, today)
                    binding.tvKidDate.text = daysBetween.toString()

                    val imageView: ImageView = binding.imgView
                    var s3Url = body?.s3Url.toString()

                    Glide.with(this@MainActivity)
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
}