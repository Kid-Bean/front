package soongsil.kidbean.front.quiz.word.ui

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
import soongsil.kidbean.front.databinding.ActivityWordQuizUpdateBinding
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.quiz.MyQuizActivity
import soongsil.kidbean.front.quiz.QuizListActivity
import soongsil.kidbean.front.quiz.word.dto.request.WordQuizUpdateRequest
import soongsil.kidbean.front.quiz.word.presentation.WordQuizController

class WordQuizUpdateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWordQuizUpdateBinding
    private lateinit var title: String
    private lateinit var answer: String
    private lateinit var word1 : String
    private lateinit var word2 : String
    private lateinit var word3 : String
    private lateinit var word4 : String
    private var quizId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityWordQuizUpdateBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, WordQuizListActivity::class.java)
            startActivity(intent)
            finish()
        }

        initSetting()

        //  등록 버튼 눌렀을 때 팝업 띄우기
        binding.btnEnroll.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("단어 문제 수정")
                setMessage("문제를 수정하시겠습니까?")
                setNegativeButton("취소") { _, _ ->
                    Toast.makeText(this@WordQuizUpdateActivity, "수정을 취소하였습니다.", Toast.LENGTH_SHORT)
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

    private fun initSetting() {
        quizId = intent.getLongExtra("quizId", -1L)
        title = intent.getStringExtra("title").toString()
        answer = intent.getStringExtra("answer").toString()
        word1 = intent.getStringExtra("word1").toString()
        word2 = intent.getStringExtra("word2").toString()
        word3 = intent.getStringExtra("word3").toString()
        word4 = intent.getStringExtra("word4").toString()

        binding.tvTitle.setText(title)
        binding.tvCorrect.setText(answer)
        binding.tvWord1.setText(word1)
        binding.tvWord2.setText(word2)
        binding.tvWord3.setText(word3)
        binding.tvWord4.setText(word4)
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
            /*val intent = Intent(this, ProgramActivity::class.java)
            startActivity(intent)*/
        }

        // 마이페이지 화면으로 변경하기!
        binding.btnProgram.setOnClickListener {
            /*val intent = Intent(this, MypageActivity::class.java)
            startActivity(intent)*/
        }
    }

    private fun loadInfo() {
        title = binding.tvTitle.text.toString()
        answer = binding.tvCorrect.text.toString()
        word1 = binding.tvWord1.text.toString()
        word2 = binding.tvWord2.text.toString()
        word3 = binding.tvWord3.text.toString()
        word4 = binding.tvWord4.text.toString()

        val words = mutableListOf<String>(word1, word2, word3, word4)

        val wordQuizController = retrofit.create(WordQuizController::class.java)
        wordQuizController.updateWordQuiz(1, quizId, WordQuizUpdateRequest(title, answer, words)).enqueue(object :
            Callback<ResponseTemplate<Void>> {
            override fun onResponse(
                call: Call<ResponseTemplate<Void>>,
                response: Response<ResponseTemplate<Void>>,
            ) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    Log.d("post", "onResponse 성공: " + response.body().toString())

                    Toast.makeText(this@WordQuizUpdateActivity, "수정이 완료되었습니다.", Toast.LENGTH_SHORT)
                        .show()

                    // 통신이 성공하면 Activity를 종료
                    finish()

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("post", "onResponse 실패 + ${response.code()}")
                    Toast.makeText(this@WordQuizUpdateActivity, "수정이 실패하였습니다.", Toast.LENGTH_SHORT)
                        .show()
                }

                // MyQuizActivity로 이동
                val intent = Intent(this@WordQuizUpdateActivity, MyQuizActivity::class.java)
                startActivity(intent)
            }

            override fun onFailure(call: Call<ResponseTemplate<Void>>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("post", "onFailure 에러: " + t.message.toString())
            }
        })
    }
}