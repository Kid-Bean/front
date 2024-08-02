package soongsil.kidbean.front.quiz.answer.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import soongsil.kidbean.front.home.ui.MainActivity
import soongsil.kidbean.front.databinding.ActivityAnswerQuizUpdateBinding
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.mypage.MypageActivity
import soongsil.kidbean.front.program.ui.ProgramHomeActivity
import soongsil.kidbean.front.quiz.QuizListActivity
import soongsil.kidbean.front.quiz.answer.dto.request.AnswerQuizUpdateRequest
import soongsil.kidbean.front.quiz.answer.presentation.AnswerQuizController
import soongsil.kidbean.front.util.ApiClient

class AnswerQuizUpdateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAnswerQuizUpdateBinding
    private lateinit var title: String
    private lateinit var question: String
    private var quizId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAnswerQuizUpdateBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        ApiClient.init(this)

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, AnswerQuizListActivity::class.java)
            startActivity(intent)
            finish()
        }

        quizId = intent.getLongExtra("quizId", -1L)
        title = intent.getStringExtra("title").toString()
        question = intent.getStringExtra("question").toString()

        binding.tvTitle.setText(title)
        binding.tvQuestion.setText(question)

        //  등록 버튼 눌렀을 때 팝업 띄우기
        binding.btnEnroll.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("단어 문제 수정")
                setMessage("문제를 수정하시겠습니까?")
                setNegativeButton("취소") { _, _ ->
                    Toast.makeText(this@AnswerQuizUpdateActivity, "수정을 취소하였습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
                setPositiveButton("수정") { _, _ ->
                    loadInfo()
                    finish()
                }
            }.create().show()
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
            val intent = Intent(this, ProgramHomeActivity::class.java)
            startActivity(intent)
        }

        // 마이페이지 화면으로 변경하기!
        binding.btnProgram.setOnClickListener {
            val intent = Intent(this, MypageActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadInfo() {
        title = binding.tvTitle.text.toString()
        question = binding.tvQuestion.text.toString()

        val answerQuizController = ApiClient.getApiClient().create(AnswerQuizController::class.java)
        answerQuizController.updateAnswerQuiz(quizId, AnswerQuizUpdateRequest(title, question)).enqueue(object :
            Callback<ResponseTemplate<Void>> {
            override fun onResponse(
                call: Call<ResponseTemplate<Void>>,
                response: Response<ResponseTemplate<Void>>,
            ) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    Log.d("post", "onResponse 성공: " + response.body().toString())

                    Toast.makeText(this@AnswerQuizUpdateActivity, "수정이 완료되었습니다.", Toast.LENGTH_SHORT)
                        .show()

                    // 통신이 성공하면 Activity를 종료
                    finish()

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("post", "onResponse 실패 + ${response.code()}")
                    Toast.makeText(this@AnswerQuizUpdateActivity, "수정이 실패하였습니다.", Toast.LENGTH_SHORT)
                        .show()
                }

                // MyQuizActivity로 이동
                val intent = Intent(this@AnswerQuizUpdateActivity, MypageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }

            override fun onFailure(call: Call<ResponseTemplate<Void>>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("post", "onFailure 에러: " + t.message.toString())
            }
        })
    }
}