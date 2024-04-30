package soongsil.kidbean.front.mypage

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
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.mypage.image.dto.response.SolvedImageDetailResponse
import soongsil.kidbean.front.mypage.image.presentation.MypageImageController
import soongsil.kidbean.front.quiz.QuizListActivity
import soongsil.kidbean.front.util.ApiClient

class ImageQuizSolvedDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageQuizSolvedResultBinding
    private var solvedId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageQuizSolvedResultBinding.inflate(layoutInflater)
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
        myPageImageController.getImageQuizDetail(solvedId).enqueue(object :
            Callback<ResponseTemplate<SolvedImageDetailResponse>> {
            override fun onResponse(
                call: Call<ResponseTemplate<SolvedImageDetailResponse>>,
                response: Response<ResponseTemplate<SolvedImageDetailResponse>>
            ) {
                if (response.isSuccessful) {
                    Log.d("post", "onResponse 성공: " + response.body().toString())
                    val body = response.body()?.results
                        ?: throw IllegalStateException("Response body is null")

                    bindDate(body)
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("post", "onResponse 실패 + ${response.code()}")
                }
            }

            override fun onFailure(
                call: Call<ResponseTemplate<SolvedImageDetailResponse>>, t: Throwable
            ) {
                Log.d("post", "onResponse 실패 + ${t.message}")
            }
        })
    }

    private fun bindDate(body: SolvedImageDetailResponse) {
        binding.tvAnswer.text = body.answer
        binding.tvReply.text = body.kidAnswer
        val imageView: ImageView = binding.imgQuiz
        Glide.with(this@ImageQuizSolvedDetailActivity)
            .load(body.imageUrl)
            .into(imageView)
        imageView.visibility = View.VISIBLE
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
