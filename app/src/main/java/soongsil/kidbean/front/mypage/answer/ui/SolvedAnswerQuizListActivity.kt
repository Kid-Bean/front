package soongsil.kidbean.front.mypage.answer.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import soongsil.kidbean.front.databinding.ActivityAnswerQuizSolvedListBinding
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.home.ui.MainActivity
import soongsil.kidbean.front.mypage.MySolvedQuizActivity
import soongsil.kidbean.front.mypage.answer.dto.response.SolvedAnswerQuizListResponse
import soongsil.kidbean.front.mypage.presentation.MypageController
import soongsil.kidbean.front.program.ui.ProgramHomeActivity
import soongsil.kidbean.front.quiz.QuizListActivity
import soongsil.kidbean.front.util.ApiClient

class SolvedAnswerQuizListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAnswerQuizSolvedListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnswerQuizSolvedListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ApiClient.init(this)

        bottomSetting()
        loadQuizList()

        binding.btnBack.setOnClickListener {
            // 홈 화면으로 이동
            val intent = Intent(this, SolvedAnswerQuizMainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setAdapter(
        quizList: List<SolvedAnswerQuizListResponse.SolvedAnswerQuizInfo>,
    ) {
        val listAdapter = SolvedAnswerQuizListAdapter(quizList)
        val linearLayoutManager = LinearLayoutManager(this)

        binding.rvQuiz.adapter = listAdapter
        binding.rvQuiz.layoutManager = linearLayoutManager
        binding.rvQuiz.setHasFixedSize(true)
    }

    private fun loadQuizList() {
        val myPageController =
            ApiClient.getApiClient().create(MypageController::class.java)
        myPageController.getAnswerQUizList().enqueue(object :
            Callback<ResponseTemplate<SolvedAnswerQuizListResponse>> {
            override fun onResponse(
                call: Call<ResponseTemplate<SolvedAnswerQuizListResponse>>,
                response: Response<ResponseTemplate<SolvedAnswerQuizListResponse>>
            ) {
                if (response.isSuccessful) {
                    Log.d("post", "onResponse 성공: " + response.body().toString())
                    val body = response.body()?.results
                        ?: throw IllegalStateException("Response body is null")

                    setAdapter(body.solvedList)
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("post", "onResponse 실패 + ${response.code()}")
                }
            }

            override fun onFailure(
                call: Call<ResponseTemplate<SolvedAnswerQuizListResponse>>,
                t: Throwable
            ) {
                Log.d("post", "onResponse 실패 + ${t.message}")
            }

        })
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
