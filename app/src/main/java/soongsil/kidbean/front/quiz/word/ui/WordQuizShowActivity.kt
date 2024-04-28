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
import soongsil.kidbean.front.databinding.ActivityWordQuizShowBinding
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.mypage.MypageActivity
import soongsil.kidbean.front.quiz.MyQuizActivity
import soongsil.kidbean.front.quiz.QuizListActivity
import soongsil.kidbean.front.quiz.word.dto.response.WordQuizMemberDetailResponse
import soongsil.kidbean.front.quiz.word.presentation.WordQuizController
import soongsil.kidbean.front.util.ApiClient

class WordQuizShowActivity : AppCompatActivity() {
    private lateinit var binding : ActivityWordQuizShowBinding
    private lateinit var title : String
    private lateinit var answer : String
    private lateinit var word1 : String
    private lateinit var word2 : String
    private lateinit var word3 : String
    private lateinit var word4 : String
    private var quizId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityWordQuizShowBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        ApiClient.init(this)

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, WordQuizListActivity::class.java)
            startActivity(intent)
        }

        quizId = intent.getLongExtra("quizId", 1)

        loadInfo()

        // 수정 버튼 눌렀을 때 수정 화면으로 이동
        binding.btnEdit.setOnClickListener {
            // 그림 문제 목록 화면으로 이동
            val intent = Intent(this, WordQuizUpdateActivity::class.java)
            intent.putExtra("title", title)
            intent.putExtra("answer", answer)
            intent.putExtra("quizId", quizId)
            intent.putExtra("word1", word1)
            intent.putExtra("word2", word2)
            intent.putExtra("word3", word3)
            intent.putExtra("word4", word4)
            startActivity(intent)
        }

        // 삭제 버튼 눌렀을 때 팝업 띄우기
        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("그림 문제 삭제")
                setMessage("문제를 삭제하시겠습니까?")
                setNegativeButton("취소") { _, _ ->
                    Toast.makeText(this@WordQuizShowActivity, "삭제를 취소하였습니다.", Toast.LENGTH_SHORT).show()
                }
                setPositiveButton("삭제") { _, _ ->
                    postDelete()
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
            val intent = Intent(this, MypageActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadInfo() {
        val wordQuizController = ApiClient.getApiClient().create(WordQuizController::class.java)
        wordQuizController.getAnswerQuizById(quizId).enqueue(object :
            Callback<ResponseTemplate<WordQuizMemberDetailResponse>> {
            override fun onResponse(
                call: Call<ResponseTemplate<WordQuizMemberDetailResponse>>,
                response: Response<ResponseTemplate<WordQuizMemberDetailResponse>>,
            ) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    Log.d("post", "onResponse 성공: " + response.body().toString())

                    val body = response.body()?.results
                    val words = body?.words

                    // API로 가져온 제목 넣기
                    title = body?.title.toString()
                    binding.tvTitle.text = title

                    word1 = words!![0].content
                    word2 = words[1].content
                    word3 = words[2].content
                    word4 = words[3].content

                    binding.tvWord1.text = word1
                    binding.tvWord2.text = word2
                    binding.tvWord3.text = word3
                    binding.tvWord4.text = word4

                    // API로 가져온 정답 넣기
                    answer = body.answer
                    binding.tvCorrect.text = answer

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("post", "onResponse 실패 + ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseTemplate<WordQuizMemberDetailResponse>>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("post", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    private fun postDelete() {
        val wordQuizController = ApiClient.getApiClient().create(WordQuizController::class.java)
        wordQuizController.deleteWordQuiz(quizId).enqueue(object :
            Callback<ResponseTemplate<Void>> {
            override fun onResponse(
                call: Call<ResponseTemplate<Void>>,
                response: Response<ResponseTemplate<Void>>,
            ) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    Log.d("post", "onResponse 성공: " + response.body().toString())
                    Toast.makeText(this@WordQuizShowActivity, "삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("post", "onResponse 실패 + ${response.code()}")
                }

                // MyQuizActivity로 이동
                val intent = Intent(this@WordQuizShowActivity, MyQuizActivity::class.java)
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