package soongsil.kidbean.front.quiz.answer.ui

import RetrofitImpl.retrofit
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import soongsil.kidbean.front.MainActivity
import soongsil.kidbean.front.databinding.ActivityAnswerQuizShowBinding
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.quiz.MyQuizActivity
import soongsil.kidbean.front.quiz.QuizListActivity
import soongsil.kidbean.front.quiz.answer.dto.response.AnswerQuizMemberDetailResponse
import soongsil.kidbean.front.quiz.answer.presentation.AnswerQuizController

class AnswerQuizShowActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAnswerQuizShowBinding
    private lateinit var title : String
    private lateinit var question : String
    private var quizId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAnswerQuizShowBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, AnswerQuizListActivity::class.java)
            startActivity(intent)
        }

        quizId = intent.getLongExtra("quizId", 1)

        loadInfo()

        // 수정 버튼 눌렀을 때 수정 화면으로 이동
        binding.btnEdit.setOnClickListener {
            // 그림 문제 목록 화면으로 이동
            val intent = Intent(this, AnswerQuizUpdateActivity::class.java)
            intent.putExtra("title", title)
            intent.putExtra("answer", question)
            intent.putExtra("quizId", quizId)
            startActivity(intent)
        }

        // 삭제 버튼 눌렀을 때 팝업 띄우기
        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("그림 문제 삭제")
                setMessage("문제를 삭제하시겠습니까?")
                setNegativeButton("취소") { _, _ ->
                    Toast.makeText(this@AnswerQuizShowActivity, "삭제를 취소하였습니다.", Toast.LENGTH_SHORT).show()
                }
                setPositiveButton("삭제") { _, _ ->
                    postDelete()

                    val intent = Intent(this@AnswerQuizShowActivity, MyQuizActivity::class.java)
                    startActivity(intent)

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
            /*val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)*/
        }

        // 마이페이지 화면으로 변경하기!
        binding.btnProgram.setOnClickListener {
            /*val intent = Intent(this, MypageActivity::class.java)
            startActivity(intent)*/
        }
    }

    private fun loadInfo() {
        val answerQuizController = retrofit.create(AnswerQuizController::class.java)
        answerQuizController.getAnswerQuizById(1, quizId).enqueue(object :
            Callback<ResponseTemplate<AnswerQuizMemberDetailResponse>> {
            override fun onResponse(
                call: Call<ResponseTemplate<AnswerQuizMemberDetailResponse>>,
                response: Response<ResponseTemplate<AnswerQuizMemberDetailResponse>>,
            ) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    Log.d("post", "onResponse 성공: " + response.body().toString())

                    val body = response.body()?.results

                    // API로 가져온 제목 넣기
                    title = body?.title.toString()
                    binding.tvTitle.text = title

                    // API로 가져온 질문 넣기
                    question = body?.question.toString()
                    binding.tvQuestion.text = question

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("post", "onResponse 실패 + ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseTemplate<AnswerQuizMemberDetailResponse>>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("post", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    private fun postDelete() {
        val answerQuizController = retrofit.create(AnswerQuizController::class.java)
        answerQuizController.deleteAnswerQuiz(1, quizId).enqueue(object :
            Callback<ResponseTemplate<Void>> {
            override fun onResponse(
                call: Call<ResponseTemplate<Void>>,
                response: Response<ResponseTemplate<Void>>,
            ) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    Log.d("post", "onResponse 성공: " + response.body().toString())
                    Toast.makeText(this@AnswerQuizShowActivity, "삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show()

                    // 통신이 성공하면 Activity를 종료
                    finish()
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("post", "onResponse 실패 + ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseTemplate<Void>>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("post", "onFailure 에러: " + t.message.toString())
            }
        })
    }
}