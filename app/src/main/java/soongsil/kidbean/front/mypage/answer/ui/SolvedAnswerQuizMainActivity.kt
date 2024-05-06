package soongsil.kidbean.front.mypage.answer.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import soongsil.kidbean.front.databinding.ActivityMyAnswerQuizSolvedMainBinding
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.home.ui.MainActivity
import soongsil.kidbean.front.mypage.MySolvedQuizActivity
import soongsil.kidbean.front.mypage.answer.dto.response.AllUseWordResponse
import soongsil.kidbean.front.mypage.presentation.MypageController
import soongsil.kidbean.front.quiz.QuizListActivity
import soongsil.kidbean.front.util.ApiClient

class SolvedAnswerQuizMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyAnswerQuizSolvedMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyAnswerQuizSolvedMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ApiClient.init(this)

        binding.btnBack.setOnClickListener {
            // 홈 화면으로 이동
            val intent = Intent(this, MySolvedQuizActivity::class.java)
            startActivity(intent)
        }

        binding.btnShowList.setOnClickListener {
            // 문제 풀이 리스트 화면으로 이동
            val intent = Intent(this, SolvedAnswerQuizListActivity::class.java)
            startActivity(intent)
        }

        bottomSetting()

        bindData()
    }

    private fun bindData() {
        val myPageController =
            ApiClient.getApiClient().create(MypageController::class.java)
        myPageController.getAllUseWordList().enqueue(object :
            Callback<ResponseTemplate<AllUseWordResponse>> {
            override fun onResponse(
                call: Call<ResponseTemplate<AllUseWordResponse>>,
                response: Response<ResponseTemplate<AllUseWordResponse>>
            ) {
                if (response.isSuccessful) {
                    Log.d("post", "onResponse 성공: " + response.body().toString())
                    val body = response.body()?.results
                        ?: throw IllegalStateException("Response body is null")

                    drawBarData(body.wordInfoList)
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("post", "onResponse 실패 + ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseTemplate<AllUseWordResponse>>, t: Throwable) {
                Log.d("post", "onResponse 실패 + ${t.message}")
            }
        })
    }

    private fun drawBarData(wordInfoList: List<AllUseWordResponse.UseWordInfo>) {
        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()

        wordInfoList.forEachIndexed { index, wordInfo ->
            val barEntry = BarEntry(index.toFloat(), wordInfo.count.toFloat())
            entries.add(barEntry)
            labels.add(wordInfo.word) // X축에 표시될 레이블 추가
        }

        val dataSet = BarDataSet(entries, "Word Count")
        val barData = BarData(dataSet)

        binding.chart.apply {
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            xAxis.labelCount = 5

            dataSet.color = Color.parseColor("#4CAF50")

            data = barData
            invalidate()
        }

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
            val intent = Intent(this, SolvedAnswerQuizMainActivity::class.java)
            startActivity(intent)
        }
    }

}