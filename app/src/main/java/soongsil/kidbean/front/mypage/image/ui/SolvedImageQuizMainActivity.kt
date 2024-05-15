package soongsil.kidbean.front.mypage.image.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import soongsil.kidbean.front.databinding.ActivityMyImageQuizSolvedMainBinding
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.home.ui.MainActivity
import soongsil.kidbean.front.mypage.MySolvedQuizActivity
import soongsil.kidbean.front.mypage.image.dto.response.MyPageImageScoreResponse
import soongsil.kidbean.front.mypage.presentation.MypageController
import soongsil.kidbean.front.mypage.main.dto.QuizCategory
import soongsil.kidbean.front.program.ui.ProgramHomeActivity
import soongsil.kidbean.front.quiz.QuizListActivity
import soongsil.kidbean.front.util.ApiClient

class SolvedImageQuizMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyImageQuizSolvedMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyImageQuizSolvedMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ApiClient.init(this)

        makeScoreChart()
        bottomSetting()

        binding.btnBack.setOnClickListener {
            // 홈 화면으로 이동
            val intent = Intent(this, MySolvedQuizActivity::class.java)
            startActivity(intent)
        }

        binding.btnRightQuiz.setOnClickListener{
            val intent = Intent(this, RightQuizListActivity::class.java)
            intent.putExtra("isRight", "right")
            startActivity(intent)
        }

        binding.btnWrongQuiz.setOnClickListener{
            val intent = Intent(this, WrongQuizListActivity::class.java)
            intent.putExtra("isRight", "wrong")
            startActivity(intent)
        }
    }

    private fun makeScoreChart() {
        val myPageController =
            ApiClient.getApiClient().create(MypageController::class.java)
        myPageController.getImageScoreResult().enqueue(object :
            Callback<ResponseTemplate<MyPageImageScoreResponse>> {
            override fun onResponse(
                call: Call<ResponseTemplate<MyPageImageScoreResponse>>,
                response: Response<ResponseTemplate<MyPageImageScoreResponse>>
            ) {
                if (response.isSuccessful) {
                    Log.d("post", "onResponse 성공: " + response.body().toString())
                    val body = response.body()?.results
                        ?: throw IllegalStateException("Response body is null")

                    setScoreChart(body)
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("post", "onResponse 실패 + ${response.code()}")
                }
            }

            override fun onFailure(
                call: Call<ResponseTemplate<MyPageImageScoreResponse>>, t: Throwable
            ) {
                Log.d("post", "onResponse 실패 + ${t.message}")
            }

        })
    }

    private fun setScoreChart(body: MyPageImageScoreResponse) {
        val myScoreChartEntry = ArrayList<Entry>()
        val ageScoreChartEntry = ArrayList<Entry>()

        val categories = QuizCategory.values()
        categories.forEach { category ->
            val matchingMyScoreInfo = body.myScoreInfo.find { it.quizCategory == category.toString() }
            val score = matchingMyScoreInfo?.score?.toFloat() ?: 0f
            myScoreChartEntry.add(Entry(getCategoryCode(category.toString()), score))
        }

        body.ageScoreInfo.forEach { ageScoreInfo ->
            val averageScore = if (ageScoreInfo.memberCount == 0L) {
                0f
            } else {
                ageScoreInfo.score.toFloat() / ageScoreInfo.memberCount
            }
            ageScoreChartEntry.add(
                Entry(
                    getCategoryCode(ageScoreInfo.quizCategory),
                    averageScore
                )
            )
        }

        val myScoreLineDataSet = LineDataSet(myScoreChartEntry, "내 아이 점수")
        val ageScoreLineDataSet = LineDataSet(ageScoreChartEntry, "평균 아이 점수")
        myScoreLineDataSet.color = Color.GREEN
        ageScoreLineDataSet.color = Color.RED

        binding.imageScoreChart.apply {
            data = LineData(myScoreLineDataSet, ageScoreLineDataSet)
            description.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.labelCount = 4
            xAxis.granularity = 1f
            xAxis.setDrawGridLines(false)

            val xLabels = listOf(0f to "동물", 1f to "식물", 2f to "사물", 3f to "음식", 4f to "기타")
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val label = xLabels.find { it.first == value }
                    return label?.second ?: ""
                }
            }
            invalidate()
        }
    }

    private fun getCategoryCode(quizCategory: String) =
        (QuizCategory.valueOf(quizCategory).getCategoryCode())

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
            val intent = Intent(this, ProgramHomeActivity::class.java)
            startActivity(intent)
        }

        // 마이페이지 화면으로 변경하기!
        binding.btnMypage.setOnClickListener {
            val intent = Intent(this, MySolvedQuizActivity::class.java)
            startActivity(intent)
        }
    }
}