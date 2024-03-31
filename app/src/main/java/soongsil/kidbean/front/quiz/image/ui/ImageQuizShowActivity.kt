package soongsil.kidbean.front.quiz.image.ui

import RetrofitImpl.retrofit
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
import soongsil.kidbean.front.quiz.image.presentation.ImageQuizController
import soongsil.kidbean.front.quiz.image.dto.response.ImageQuizMemberDetailResponse
import soongsil.kidbean.front.databinding.ActivityImageQuizShowBinding


class ImageQuizShowActivity : AppCompatActivity() {
    private lateinit var binding : ActivityImageQuizShowBinding
    private lateinit var title : String
    private lateinit var imgUrl : String
    private lateinit var answer : String
    private lateinit var category: String

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityImageQuizShowBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            // 그림 문제 목록 화면으로 이동
            //val intent = Intent(this, ImageQuizListActivity::class.java)
            startActivity(intent)
        }

        loadInfo()

        // 수정 버튼 눌렀을 때 수정 화면으로 이동
        binding.btnEdit.setOnClickListener {
            // 그림 문제 목록 화면으로 이동
            val intent = Intent(this, ImageQuizUpdateActivity::class.java)
            intent.putExtra("edit", -1)
            intent.putExtra("title", title)
            intent.putExtra("imgUrl", imgUrl)
            intent.putExtra("answer", answer)
            startActivity(intent)
        }

        // 삭제 버튼 눌렀을 때 팝업 띄우기
        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("그림 문제 추가 삭제")
                setMessage("작성한 문제를 삭제하시겠습니까?")
                setNegativeButton("취소") { _, _ ->
                    Toast.makeText(this@ImageQuizShowActivity, "삭제를 취소하였습니다.", Toast.LENGTH_SHORT).show()
                }
                setPositiveButton("삭제") { _, _ ->
                    Toast.makeText(this@ImageQuizShowActivity, "삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }.create().show()

            finish()
        }
    }

    private fun loadInfo() {
        val imageQuizController = retrofit.create(ImageQuizController::class.java)
        imageQuizController.getImageQuizById(1, 7).enqueue(object :
            Callback<ImageQuizMemberDetailResponse> {
            override fun onResponse(
                call: Call<ImageQuizMemberDetailResponse>,
                response: Response<ImageQuizMemberDetailResponse>,
            ) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    Log.d("post", "onResponse 성공: " + response.body().toString())

                    val body = response.body()

                    // API로 가져온 제목 넣기
                    title = body?.title.toString()
                    binding.tvTitle.text = title

                    // API로 가져온 이미지 넣기
                    val imageView: ImageView = binding.imgQuiz
                    imgUrl = body?.imgUrl.toString()

                    Glide.with(this@ImageQuizShowActivity)
                        .load(imgUrl)
                        .into(imageView)
                    imageView.visibility = View.VISIBLE

                    // API로 가져온 카테고리 넣기
                    category = body?.category.toString()
                    if (category.equals("ANIMAL")) {
                        category = "동물"
                    }
                    else if (category.equals("PLANT")) {
                        category = "식물"
                    }
                    else if (category.equals("FOOD")) {
                        category = "음식"
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

            override fun onFailure(call: Call<ImageQuizMemberDetailResponse>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("post", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    override fun onRestart() {
        super.onRestart()
        loadInfo()
    }

    override fun onResume() {
        super.onResume()
        loadInfo()
    }
}