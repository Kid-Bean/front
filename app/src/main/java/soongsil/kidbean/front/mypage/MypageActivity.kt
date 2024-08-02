package soongsil.kidbean.front.mypage

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import soongsil.kidbean.front.home.ui.MainActivity
import soongsil.kidbean.front.databinding.ActivityMypageBinding
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.member.presentation.MemberController
import soongsil.kidbean.front.mypage.answer.ui.SolvedAnswerQuizListActivity
import soongsil.kidbean.front.mypage.image.ui.SolvedImageQuizMainActivity
import soongsil.kidbean.front.mypage.main.dto.response.MemberInfoResponse
import soongsil.kidbean.front.mypage.word.ui.SolvedWordQuizListActivity
import soongsil.kidbean.front.program.ui.ProgramHomeActivity
import soongsil.kidbean.front.quiz.QuizListActivity
import soongsil.kidbean.front.quiz.answer.ui.AnswerQuizListActivity
import soongsil.kidbean.front.quiz.image.ui.ImageQuizListActivity
import soongsil.kidbean.front.quiz.word.ui.WordQuizListActivity
import soongsil.kidbean.front.util.ApiClient

class MypageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMypageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ApiClient.init(this)

        binding.btnBack.setOnClickListener {
            // 홈 화면으로 이동
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnChangeInfo.setOnClickListener {
            // 사용자 정보 변경 창
            val intent = Intent(this, UpdateKidInfoActivity::class.java)
            startActivity(intent)
        }

        binding.enrollBtnImage.setOnClickListener {
            val intent = Intent(this, ImageQuizListActivity::class.java)
            startActivity(intent)
        }
        binding.enrollBtnWord.setOnClickListener {
            val intent = Intent(this, WordQuizListActivity::class.java)
            startActivity(intent)
        }
        binding.enrollBtnAnswer.setOnClickListener {
            val intent = Intent(this, AnswerQuizListActivity::class.java)
            startActivity(intent)
        }

        binding.btnImage.setOnClickListener {
            // 이미지 퀴즈 메인
            val intent = Intent(this, SolvedImageQuizMainActivity::class.java)
            startActivity(intent)
        }
        binding.btnWord.setOnClickListener {
            //단어 퀴즈 메인
            val intent = Intent(this, SolvedWordQuizListActivity::class.java)
            startActivity(intent)
        }
        binding.btnVoice.setOnClickListener {
            //음성 퀴즈 메인
            val intent = Intent(this, SolvedAnswerQuizListActivity::class.java)
            startActivity(intent)
        }

        loadMemberInfo()

        bottomSetting()
    }

    private fun loadMemberInfo() {
        val myPageController = ApiClient.getApiClient().create(MemberController::class.java)
        myPageController.getImageQuizById().enqueue(object :
            Callback<ResponseTemplate<MemberInfoResponse>> {
            override fun onResponse(
                call: Call<ResponseTemplate<MemberInfoResponse>>,
                response: Response<ResponseTemplate<MemberInfoResponse>>
            ) {
                if (response.isSuccessful) {
                    Log.d("post", "onResponse 성공: " + response.body().toString())
                    val body = response.body()?.results

                    setMemberInfo(body)

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("post", "onResponse 실패 + ${response.code()}")
                }
            }

            override fun onFailure(
                call: Call<ResponseTemplate<MemberInfoResponse>>, t: Throwable
            ) {
                Log.d("post", "onResponse 실패 + ${t.message}")
            }

        })
    }

    @SuppressLint("SetTextI18n")
    private fun setMemberInfo(body: MemberInfoResponse?) {
        binding.textName.text = body?.memberName ?: "금쪽이"
        binding.textBirth.text = "${body?.memberBirth.toString()} (만 ${body?.memberAge}세)"
        binding.textGender.text = body?.memberGender?.toGenderString()
    }

    private fun String?.toGenderString(): String {
        return when (this) {
            "MAN" -> "남자"
            "WOMAN" -> "여자"
            else -> "" // 다른 값이 올 경우 처리 가능
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