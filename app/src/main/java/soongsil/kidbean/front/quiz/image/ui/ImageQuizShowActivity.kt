package soongsil.kidbean.front.quiz.image.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import soongsil.kidbean.front.home.ui.MainActivity
import soongsil.kidbean.front.databinding.ActivityImageQuizShowBinding
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.mypage.MypageActivity
import soongsil.kidbean.front.program.ui.ProgramHomeActivity
import soongsil.kidbean.front.quiz.QuizListActivity
import soongsil.kidbean.front.quiz.image.dto.response.ImageQuizMemberDetailResponse
import soongsil.kidbean.front.quiz.image.presentation.ImageQuizController
import soongsil.kidbean.front.util.ApiClient


class ImageQuizShowActivity : AppCompatActivity() {
    private lateinit var binding : ActivityImageQuizShowBinding
    private lateinit var title : String
    private lateinit var s3Url : String
    private lateinit var answer : String
    private lateinit var category: String
    private var quizId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityImageQuizShowBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        ApiClient.init(this)

        binding.btnBack.setOnClickListener {
            // 그림 문제 목록 화면으로 이동
            val intent = Intent(this, ImageQuizListActivity::class.java)
            startActivity(intent)
        }

        quizId = intent.getLongExtra("quizId", -1L)

        loadInfo()

        // 수정 버튼 눌렀을 때 수정 화면으로 이동
        binding.btnEdit.setOnClickListener {
            // 그림 문제 목록 화면으로 이동
            val intent = Intent(this, ImageQuizUpdateActivity::class.java)
            intent.putExtra("quizId", quizId)
            intent.putExtra("title", title)
            intent.putExtra("imgUrl", s3Url)
            intent.putExtra("answer", answer)
            startActivity(intent)
        }

        // 삭제 버튼 눌렀을 때 팝업 띄우기
        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("그림 문제 삭제")
                setMessage("문제를 삭제하시겠습니까?")
                setNegativeButton("취소") { _, _ ->
                    Toast.makeText(this@ImageQuizShowActivity, "삭제를 취소하였습니다.", Toast.LENGTH_SHORT).show()
                }
                setPositiveButton("삭제") { _, _ ->
                    postDelete()

                    val intent = Intent(this@ImageQuizShowActivity, MypageActivity::class.java)
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
            val intent = Intent(this, ProgramHomeActivity::class.java)
            startActivity(intent)
        }

        // 마이페이지 화면으로 변경하기!
        binding.btnMypage.setOnClickListener {
            val intent = Intent(this, MypageActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadInfo() {
        val imageQuizController = ApiClient.getApiClient().create(ImageQuizController::class.java)
        imageQuizController.getImageQuizById(quizId).enqueue(object :
            Callback<ResponseTemplate<ImageQuizMemberDetailResponse>> {
            override fun onResponse(
                call: Call<ResponseTemplate<ImageQuizMemberDetailResponse>>,
                response: Response<ResponseTemplate<ImageQuizMemberDetailResponse>>,
            ) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    Log.d("post", "onResponse 성공: " + response.body().toString())

                    val body = response.body()?.results

                    // API로 가져온 제목 넣기
                    title = body?.title.toString()
                    binding.tvTitle.text = title

                    // API로 가져온 이미지 넣기
                    val imageView: ImageView = binding.imgQuiz
                    s3Url = body?.s3Url.toString()

                    Glide.with(this@ImageQuizShowActivity)
                        .load(s3Url)
                        .into(imageView)
                    imageView.visibility = View.VISIBLE

                    // API로 가져온 카테고리 넣기
                    category = body?.quizCategory.toString()
                    if (category.equals("ANIMAL")) {
                        category = "동물"
                    }
                    else if (category.equals("PLANT")) {
                        category = "식물"
                    }
                    else if (category.equals("OBJECT")) {
                        category = "사물"
                    }
                    else if (category.equals("FOOD")) {
                        category = "음식"
                    }
                    else if (category.equals("NONE")) {
                        category = "없음"
                    }

                    binding.tvCategoryAnswer.text = category

                    // API로 가져온 정답 넣기
                    answer = body?.answer.toString()
                    binding.tvCorrect.text = answer

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("post", "onResponse 실패 + ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseTemplate<ImageQuizMemberDetailResponse>>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("post", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    private fun postDelete() {
        val imageQuizController = ApiClient.getApiClient().create(ImageQuizController::class.java)
        imageQuizController.deleteImageQuiz(quizId).enqueue(object :
            Callback<ResponseTemplate<Void>> {
            override fun onResponse(
                call: Call<ResponseTemplate<Void>>,
                response: Response<ResponseTemplate<Void>>,
            ) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    Log.d("post", "onResponse 성공: " + response.body().toString())
                    Toast.makeText(this@ImageQuizShowActivity, "삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("post", "onResponse 실패 + ${response.code()}")
                }

                // MyQuizActivity로 이동
                val intent = Intent(this@ImageQuizShowActivity, MypageActivity::class.java)
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

    override fun onRestart() {
        super.onRestart()
    }

    override fun onResume() {
        super.onResume()
    }
}