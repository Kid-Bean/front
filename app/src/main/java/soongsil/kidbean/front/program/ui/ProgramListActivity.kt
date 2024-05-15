package soongsil.kidbean.front.program.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import soongsil.kidbean.front.databinding.ActivityProgramListBinding
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.home.ui.MainActivity
import soongsil.kidbean.front.mypage.MypageActivity
import soongsil.kidbean.front.program.dto.response.ProgramResponse
import soongsil.kidbean.front.program.dto.response.ProgramResponseList
import soongsil.kidbean.front.program.presentation.ProgramController
import soongsil.kidbean.front.quiz.QuizListActivity
import soongsil.kidbean.front.util.ApiClient

class ProgramListActivity : AppCompatActivity() {

    private lateinit var binding : ActivityProgramListBinding
    private var nowPage = 0
    private var nowCategory = "HOSPITAL"

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityProgramListBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        ApiClient.init(this)

        loadProgramList()
        bottomSetting()


        binding.btnPageBefore.setOnClickListener {
            if (nowPage > 0) {
                nowPage--
                loadProgramList()
            }
        }

        binding.btnPageAfter.setOnClickListener {
            nowPage++
            loadProgramList()
        }
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
            val intent = Intent(this, ProgramListActivity::class.java)
            startActivity(intent)
        }

        // 마이페이지 화면으로 변경하기!
        binding.btnMypage.setOnClickListener {
            val intent = Intent(this, MypageActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadProgramList() {
        val programController = ApiClient.getApiClient().create(ProgramController::class.java)
        programController.getProgramList(nowCategory, nowPage).enqueue(object :
            Callback<ResponseTemplate<ProgramResponseList>> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<ResponseTemplate<ProgramResponseList>>,
                response: Response<ResponseTemplate<ProgramResponseList>>,
            ) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    Log.d("post", "onResponse 성공: " + response.body().toString())

                    val body = response.body()?.results?.programResponseList

                    if (body!!.isNotEmpty()) {
                        setAdapter(body)
                    }

                    //뷰의 페이지 번호 변경
                    binding.tvNowPage.text = (nowPage + 1).toString()
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("post", "onResponse 실패 + ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseTemplate<ProgramResponseList>>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("post", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    private fun setAdapter(programList: List<ProgramResponse>) {
        val listAdapter = ProgramAdapter(programList)
        val linearLayoutManager = LinearLayoutManager(this)

        binding.rvProgram.adapter = listAdapter
        binding.rvProgram.layoutManager = linearLayoutManager
        binding.rvProgram.setHasFixedSize(true)
    }
}