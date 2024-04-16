package soongsil.kidbean.front.quiz.word.ui

import RetrofitImpl.retrofit
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import soongsil.kidbean.front.MainActivity
import soongsil.kidbean.front.R
import soongsil.kidbean.front.databinding.ActivityWordQuizSolveBinding
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.quiz.QuizListActivity
import soongsil.kidbean.front.quiz.word.dto.response.WordQuizSolveResponse
import soongsil.kidbean.front.quiz.word.presentation.WordQuizController

class WordQuizSolveActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWordQuizSolveBinding
    private lateinit var title: String
    private lateinit var answer: String
    private lateinit var word1: String
    private lateinit var word2: String
    private lateinit var word3: String
    private lateinit var word4: String
    private lateinit var selectAnswer: String
    private var quizId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityWordQuizSolveBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 단어 버튼에 대한 클릭 리스너 설정
        setButtonClickListener(binding.btnWord1)
        setButtonClickListener(binding.btnWord2)
        setButtonClickListener(binding.btnWord3)
        setButtonClickListener(binding.btnWord4)

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
        binding.btnProgram.setOnClickListener {
            /*val intent = Intent(this, MypageActivity::class.java)
            startActivity(intent)*/
        }
    }

    private fun loadInfo() {
        val wordQuizController = retrofit.create(WordQuizController::class.java)
        wordQuizController.getRandomWordQuizByMember(1).enqueue(object :
            Callback<ResponseTemplate<WordQuizSolveResponse>> {
            override fun onResponse(
                call: Call<ResponseTemplate<WordQuizSolveResponse>>,
                response: Response<ResponseTemplate<WordQuizSolveResponse>>,
            ) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    Log.d("post", "onResponse 성공: " + response.body().toString())

                    val body = response.body()?.results
                    val words = body?.words

                    // API로 가져온 제목 넣기
                    title = body?.title.toString()

                    word1 = words!![0].content
                    word2 = words[1].content
                    word3 = words[2].content
                    word4 = words[3].content

                    binding.btnWord1.text = word1
                    binding.btnWord2.text = word2
                    binding.btnWord3.text = word3
                    binding.btnWord4.text = word4

                    // API로 가져온 quizId 넣기
                    quizId = body.quizId

                    // API로 가져온 정답 넣기
                    answer = body.answer
                    binding.btnAnswer.text = answer

                    Log.d("text1", binding.btnWord1.text.toString())
                    Log.d("text2", binding.btnWord2.text.toString())
                    Log.d("text3", binding.btnWord3.text.toString())
                    Log.d("text4", binding.btnWord4.text.toString())

                    Log.d("quizId", quizId.toString())
                    Log.d("answer", answer)
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("post", "onResponse 실패 + ${response.code()}")
                }
            }

            override fun onFailure(
                call: Call<ResponseTemplate<WordQuizSolveResponse>>,
                t: Throwable
            ) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("post", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    private fun setButtonClickListener(button: Button) {
        button.setOnClickListener {
            // 선택된 답변을 변수에 저장
            selectAnswer = button.text.toString()

            // 정답 확인
            if (selectAnswer == answer) {
                // 정답일 경우 배경색을 파란색으로 변경
                button.backgroundTintList = ContextCompat.getColorStateList(applicationContext, R.color.green)
            } else {
                // 오답일 경우 이미 설정된 빨간색 배경을 유지
                button.backgroundTintList = ContextCompat.getColorStateList(applicationContext, R.color.red)
            }
            button.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))

            // 다른 버튼들을 비활성화
            val buttons = listOf(binding.btnWord1, binding.btnWord2, binding.btnWord3, binding.btnWord4)
            for (btn in buttons) {
                if (btn != button) { // 클릭된 버튼을 제외한 나머지 버튼
                    btn.isEnabled = false // 버튼 비활성화
                }
            }

            binding.btnAnswer.visibility = View.VISIBLE
        }
    }
}