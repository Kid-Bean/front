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
import soongsil.kidbean.front.databinding.ActivityWordQuizUploadBinding
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.mypage.MypageActivity
import soongsil.kidbean.front.quiz.MyQuizActivity
import soongsil.kidbean.front.quiz.QuizListActivity
import soongsil.kidbean.front.quiz.word.dto.request.WordQuizUploadRequest
import soongsil.kidbean.front.quiz.word.presentation.WordQuizController
import soongsil.kidbean.front.util.ApiClient

class WordQuizUploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWordQuizUploadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityWordQuizUploadBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        ApiClient.init(this)

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, WordQuizListActivity::class.java)
            startActivity(intent)
            finish()
        }

        //  등록 버튼 눌렀을 때 팝업 띄우기
        binding.btnEnroll.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("단어 문제 등록")
                setMessage("문제를 등록하시겠습니까?")
                setNegativeButton("취소") { _, _ ->
                    Toast.makeText(this@WordQuizUploadActivity, "등록을 취소하였습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
                setPositiveButton("등록") { _, _ ->
                    loadInfo()
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
            /*val intent = Intent(this, ProgramActivity::class.java)
            startActivity(intent)*/
        }

        // 마이페이지 화면으로 변경하기!
        binding.btnProgram.setOnClickListener {
            val intent = Intent(this, MypageActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadInfo() {
        val title = binding.tvTitle.text.toString()
        val answer = binding.tvCorrect.text.toString()
        val word1 = binding.tvWord1.text.toString()
        val word2 = binding.tvWord2.text.toString()
        val word3 = binding.tvWord3.text.toString()
        val word4 = binding.tvWord4.text.toString()

        // Words 객체 리스트 생성
        val wordList = listOf(
            WordQuizUploadRequest.Words(word1),
            WordQuizUploadRequest.Words(word2),
            WordQuizUploadRequest.Words(word3),
            WordQuizUploadRequest.Words(word4)
        )


        val wordQuizController = ApiClient.getApiClient().create(WordQuizController::class.java)
        wordQuizController.uploadWordQuiz(WordQuizUploadRequest(title, answer, wordList)).enqueue(object :
            Callback<ResponseTemplate<Void>> {
            override fun onResponse(
                call: Call<ResponseTemplate<Void>>,
                response: Response<ResponseTemplate<Void>>,
            ) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    Log.d("post", "onResponse 성공: " + response.body().toString())

                    Toast.makeText(this@WordQuizUploadActivity, "등록이 완료되었습니다.", Toast.LENGTH_SHORT)
                        .show()

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("post", "onResponse 실패 + ${response.code()}")
                    Toast.makeText(this@WordQuizUploadActivity, "등록이 실패하였습니다.", Toast.LENGTH_SHORT)
                        .show()
                }

                // MyQuizActivity로 이동
                val intent = Intent(this@WordQuizUploadActivity, MyQuizActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

                finish()
            }

            override fun onFailure(call: Call<ResponseTemplate<Void>>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("post", "onFailure 에러: " + t.message.toString())
            }
        })
    }
}