package soongsil.kidbean.front.program.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import soongsil.kidbean.front.R
import soongsil.kidbean.front.databinding.ActivityProgramDetailBinding
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.home.ui.MainActivity
import soongsil.kidbean.front.mypage.MypageActivity
import soongsil.kidbean.front.program.dto.response.ProgramDetailResponse
import soongsil.kidbean.front.program.dto.response.ProgramResponseList
import soongsil.kidbean.front.program.presentation.ProgramController
import soongsil.kidbean.front.quiz.QuizListActivity
import soongsil.kidbean.front.util.ApiClient

class ProgramDetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProgramDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityProgramDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        ApiClient.init(this)

        val programId = intent.getLongExtra("programId", 0)

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, ProgramListActivity::class.java)
            startActivity(intent)
        }

        bottomSetting()
        loadProgramInfo(programId)
    }

    private fun loadProgramInfo(programId: Long) {
        val programController = ApiClient.getApiClient().create(ProgramController::class.java)
        programController.getProgramDetail(programId).enqueue(object :
            Callback<ResponseTemplate<ProgramDetailResponse>> {
            @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
            override fun onResponse(
                call: Call<ResponseTemplate<ProgramDetailResponse>>,
                response: Response<ResponseTemplate<ProgramDetailResponse>>,
            ) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    Log.d("post", "onResponse 성공: " + response.body().toString())

                    val body = response.body()?.results

                    binding.tvProgramTitle.text = body?.programTitle

                    Glide.with(this@ProgramDetailActivity)
                        .load(body?.departmentS3Url)
                        .into(binding.imgDepartment)

                    binding.imgDepartment.visibility = View.VISIBLE

                    binding.tvDepartmentName.text = body?.departmentName
                    binding.tvLocationName.text = body?.place
                    binding.tvPhoneNumber.text = body?.phoneNumber

                    body?.date?.forEach() {
                        when (it) {
                            "월" -> {
                                binding.btnDay1.background = getDrawable(R.drawable.selected_shape_day)
                            }
                            "화" -> {
                                binding.btnDay2.background = getDrawable(R.drawable.selected_shape_day)
                            }
                            "수" -> {
                                binding.btnDay3.background = getDrawable(R.drawable.selected_shape_day)
                            }
                            "목" -> {
                                binding.btnDay4.background = getDrawable(R.drawable.selected_shape_day)
                            }
                            "금" -> {
                                binding.btnDay5.background = getDrawable(R.drawable.selected_shape_day)
                            }
                            "토" -> {
                                binding.btnDay6.background = getDrawable(R.drawable.selected_shape_day)
                            }
                            "일" -> {
                                binding.btnDay7.background = getDrawable(R.drawable.selected_shape_day)
                            }
                        }
                    }

                    binding.tvContentTitle.text = body?.contentTitle

                    Glide.with(this@ProgramDetailActivity)
                        .load(body?.programS3Url)
                        .into(binding.imgProgram)

                    binding.content.text = body?.content
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("post", "onResponse 실패 + ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseTemplate<ProgramDetailResponse>>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("post", "onFailure 에러: " + t.message.toString())
            }
        })
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
}