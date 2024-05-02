package soongsil.kidbean.front.mypage.word.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import soongsil.kidbean.front.MainActivity
import soongsil.kidbean.front.databinding.ActivityImageQuizSolvedResultBinding
import soongsil.kidbean.front.databinding.ActivityWordQuizSolvedResultBinding
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.mypage.MySolvedQuizActivity
import soongsil.kidbean.front.mypage.image.dto.response.SolvedImageDetailResponse
import soongsil.kidbean.front.mypage.image.presentation.MypageImageController
import soongsil.kidbean.front.mypage.image.ui.SolvedImageQuizMainActivity
import soongsil.kidbean.front.mypage.word.dto.response.SolvedWordDetailResponse
import soongsil.kidbean.front.quiz.QuizListActivity
import soongsil.kidbean.front.util.ApiClient

class SolvedWordQuizDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWordQuizSolvedResultBinding
    private var solvedId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWordQuizSolvedResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ApiClient.init(this)

        bottomSetting()

        solvedId = intent.getLongExtra("solvedId", -1L)

        getQuizDetail(solvedId)

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, SolvedImageQuizMainActivity::class.java)
            startActivity(intent)
        }


    }

    private fun getQuizDetail(solvedId: Long) {
        val myPageImageController =
            ApiClient.getApiClient().create(MypageImageController::class.java)
        myPageImageController.getWordQuizDetail(solvedId).enqueue(object :
            Callback<ResponseTemplate<SolvedWordDetailResponse>> {
            override fun onResponse(
                call: Call<ResponseTemplate<SolvedWordDetailResponse>>,
                response: Response<ResponseTemplate<SolvedWordDetailResponse>>
            ) {
                if (response.isSuccessful) {
                    Log.d("post", "onResponse 성공: " + response.body().toString())
                    val body = response.body()?.results
                        ?: throw IllegalStateException("Response body is null")

                    bindData(body)
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("post", "onResponse 실패 + ${response.code()}")
                }
            }

            override fun onFailure(
                call: Call<ResponseTemplate<SolvedWordDetailResponse>>, t: Throwable
            ) {
                Log.d("post", "onResponse 실패 + ${t.message}")
            }
        })
    }

    private fun bindData(body: SolvedWordDetailResponse) {
        binding.tvAnswer.text = body.answer
        binding.tvReply.text = body.kidAnswer

        val wordList = body.wordList
        binding.tvWord1.text = wordList[0]
        binding.tvWord2.text = wordList[1]
        binding.tvWord3.text = wordList[2]
        binding.tvWord4.text = wordList[3]
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
            val intent = Intent(this, MySolvedQuizActivity::class.java)
            startActivity(intent)
        }
    }
}
