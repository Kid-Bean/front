package soongsil.kidbean.front.quiz.answer.ui

import RetrofitImpl.retrofit
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import soongsil.kidbean.front.MainActivity
import soongsil.kidbean.front.databinding.ActivityAnswerQuizListBinding
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.mypage.MypageActivity
import soongsil.kidbean.front.quiz.MyQuizActivity
import soongsil.kidbean.front.quiz.QuizListActivity
import soongsil.kidbean.front.quiz.answer.dto.response.AnswerQuizMemberResponse
import soongsil.kidbean.front.quiz.answer.presentation.AnswerQuizController
import soongsil.kidbean.front.quiz.word.dto.response.WordQuizMemberResponse
import soongsil.kidbean.front.quiz.word.presentation.WordQuizController
import soongsil.kidbean.front.util.ApiClient

class AnswerQuizListActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAnswerQuizListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAnswerQuizListBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        ApiClient.init(this)

        binding.btnBack.setOnClickListener {
            // 홈 화면으로 이동
            val intent = Intent(this, MyQuizActivity::class.java)
            startActivity(intent)
        }

        binding.btnEnroll.setOnClickListener {
            // 단어 문제 등록 화면으로 이동
            val intent = Intent(this, AnswerQuizUploadActivity::class.java)
            startActivity(intent)
        }

        loadQuizList()

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
            val intent = Intent(this, MypageActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setAdapter(quizList: List<AnswerQuizMemberResponse>) {
        val listAdapter = AnswerQuizAdapter(quizList)
        val linearLayoutManager = LinearLayoutManager(this)

        binding.rvQuiz.adapter = listAdapter
        binding.rvQuiz.layoutManager = linearLayoutManager
        binding.rvQuiz.setHasFixedSize(true)
    }

    private fun loadQuizList() {
        val answerQuizController = ApiClient.getApiClient().create(AnswerQuizController::class.java)
        answerQuizController.getAllAnswerQuizByMember().enqueue(object :
            Callback<ResponseTemplate<List<AnswerQuizMemberResponse>>> {
            override fun onResponse(
                call: Call<ResponseTemplate<List<AnswerQuizMemberResponse>>>,
                response: Response<ResponseTemplate<List<AnswerQuizMemberResponse>>>,
            ) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    Log.d("post", "onResponse 성공: " + response.body().toString())

                    val body = response.body()?.results

                    if (body!!.isNotEmpty()) {
                        setAdapter(body)
                    }
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("post", "onResponse 실패 + ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseTemplate<List<AnswerQuizMemberResponse>>>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("post", "onFailure 에러: " + t.message.toString())
            }
        })
    }
}