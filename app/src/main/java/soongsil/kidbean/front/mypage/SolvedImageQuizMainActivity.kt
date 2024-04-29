package soongsil.kidbean.front.mypage

import RetrofitImpl.retrofit
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import soongsil.kidbean.front.MainActivity
import soongsil.kidbean.front.databinding.ActivityMyImageQuizSolvedMainBinding
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.mypage.image.dto.response.MyPageImageScoreResponse
import soongsil.kidbean.front.mypage.image.presentation.MypageImageController
import soongsil.kidbean.front.mypage.main.dto.QuizCategory
import soongsil.kidbean.front.quiz.MyQuizActivity
import soongsil.kidbean.front.quiz.QuizListActivity

class SolvedImageQuizMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyImageQuizSolvedMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyImageQuizSolvedMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        makeScoreChart()
        bottomSetting()

        binding.btnBack.setOnClickListener {
            // 홈 화면으로 이동
            val intent = Intent(this, MyQuizActivity::class.java)
            startActivity(intent)
        }
    }

    private fun makeScoreChart() {
        val myPageImageController = retrofit.create(MypageImageController::class.java)
        myPageImageController.getImageScoreResult(1L).enqueue(object :
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

        body.myScoreInfo.forEach { myScoreInfo ->
            myScoreChartEntry.add(
                Entry(
                    getCategoryCode(myScoreInfo.quizCategory),
                    myScoreInfo.score.toFloat()
                )
            )
        }

        body.ageScoreInfo.forEach { ageScoreInfo ->
            ageScoreChartEntry.add(
                Entry(
                    getCategoryCode(ageScoreInfo.quizCategory),
                    (ageScoreInfo.score.toFloat() / ageScoreInfo.memberCount)
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
            xAxis.setDrawGridLines(false)
            xAxis.valueFormatter = object : IndexAxisValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val values = QuizCategory.values()
                    Log.d("hello", value.toString())
                    return QuizCategory.values()[value.toInt()].getCategoryName()
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
            /*val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)*/
        }

        // 마이페이지 화면으로 변경하기!
        binding.btnMypage.setOnClickListener {
            val intent = Intent(this, MySolvedQuizActivity::class.java)
            startActivity(intent)
        }
    }
}